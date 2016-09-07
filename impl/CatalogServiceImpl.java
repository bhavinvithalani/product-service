package com.homedepot.catalog.service.impl;

import com.homedepot.cassandra.core.dao.ReadOptions;
import com.homedepot.catalog.CatalogDomainServiceConfiguration;
import com.homedepot.catalog.dao.CatalogDaoProvider;
import com.homedepot.catalog.dao.impl.itemattribute.ItemAttributeColumn;
import com.homedepot.catalog.dao.impl.itemlisting.ItemListColumn;
import com.homedepot.catalog.dao.impl.mvendorsku.MVendorSkuColumn;
import com.homedepot.catalog.exception.*;
import com.homedepot.catalog.model.ItemAttribute;
import com.homedepot.catalog.model.ItemListing;
import com.homedepot.catalog.model.MVendorSkuItem;
import com.homedepot.catalog.service.CatalogService;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Catalog service implementation
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    CatalogDaoProvider catalogDaoProvider;

    @Autowired
    CatalogDomainServiceConfiguration catalogDomainServiceConfiguration;

    @Value("${default.store}")
    private Integer onlineStoreId;
    @Value("${item.id.minimum}")
    private Integer itemIdMinimum;
    @Value("${item.id.maximum}")
    private Integer itemIdMaximum;
    @Value("${store.id.minimum}")
    private Integer storeIdMinimum;
    @Value("${store.id.maximum}")
    private Integer storeIdMaximum;
    @Value("${item.listing.max.requests}")
    private Integer itemListingMaxRequests;
    @Value("${item.attribute.max.requests}")
    private Integer itemAttributeMaxRequests;
    @Value("${item.attribute.byguids.max.guids}")
    private Integer itemAttributeByGuidsMaxGuids;
    @Value("${item.attribute.byguids.max.ids}")
    private Integer itemAttributeByGuidsMaxIds;
    @Value("${store.itemlisting.max.requests}")
    private Integer storeMaxRequests;

    private ReadOptions itemAttributeReadOptions;
    private ReadOptions mVendorSkuReadOptions;

    @PostConstruct
    public void setupReadOptions() {

        itemAttributeReadOptions = new ReadOptions();
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.ITEM_ID);
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.GUID);
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.GROUP);
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.NAME);
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.VALUE);
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.VARIANT);
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.SEQUENCE);
        itemAttributeReadOptions.addColumn(ItemAttributeColumn.SWATCH);

        mVendorSkuReadOptions = new ReadOptions();
        mVendorSkuReadOptions.addColumn(MVendorSkuColumn.ITEMID);
        mVendorSkuReadOptions.addColumn(MVendorSkuColumn.MVENDOR);
        mVendorSkuReadOptions.addColumn(MVendorSkuColumn.UNIQUE_SKU);
    }

    @Override
    public Map<Long, List<ItemAttribute>> getItemAttributes(List<Long> itemIds, String guid, String group) {
        validateAttributeItems(itemIds);

        Map<Object, List<ItemAttribute>> itemAttributes;

        if(StringUtils.isNotEmpty(group)) {
            itemAttributes = getItemAttributesByGroup(itemIds, group);
        } else if (StringUtils.isNotEmpty(guid)) {
            return getItemAttributesByGuids(itemIds, Arrays.asList(guid));
        } else {
            List<ItemAttribute> keys = itemIds.stream().map(ItemAttribute::new).collect(Collectors.toList());
            itemAttributes = catalogDaoProvider.getDAO(ItemAttribute.class).get(keys, itemAttributeReadOptions);
        }

        if(itemAttributes != null && !itemAttributes.isEmpty()) {
            Map<Long, List<ItemAttribute>> results = new HashMap<>();
            for (Map.Entry<Object, List<ItemAttribute>> entry : itemAttributes.entrySet()) {
                results.put(entry.getValue().get(0).getItemId(), entry.getValue());
            }
            return results;
        }
        return null;
    }

    @Override
    public Map<Long, List<ItemAttribute>> getItemAttributesByGuids(List<Long> itemIds, List<String> guids) {
        validateAttributeItems(itemIds);
        validateByGuidRequest(itemIds, guids);

        Set<Object[]> keys = new HashSet<>();
        String clusteringColumns[][] = new String[guids.size()][1];

        for (int i = 0; i < guids.size(); i++) {
            clusteringColumns[i][0] = guids.get(i);
        }
        for(Long itemId : itemIds) {
            keys.add(new Object[]{itemId, clusteringColumns});
        }

        Map<Object, List<ItemAttribute>> itemAttributes = catalogDaoProvider.getDAO(ItemAttribute.class).get(keys, itemAttributeReadOptions);
        if(itemAttributes != null && !itemAttributes.isEmpty()) {
            Map<Long, List<ItemAttribute>> results = new HashMap<>();
            for (Map.Entry<Object, List<ItemAttribute>> entry : itemAttributes.entrySet()) {
                results.put(entry.getValue().get(0).getItemId(), entry.getValue());
            }
            return results;
        }
        return null;
    }

    /**
     * Filters out attributes based on the given group.
     * If the group is a synthetic group, uses a different filter method than if the group is not synthetic.
     * @param itemIds the list of item ids
     * @param group   the group to filter on
     * @return the filtered item attributes
     */
    private Map<Object, List<ItemAttribute>> getItemAttributesByGroup(List<Long> itemIds, String group) {
        Map<Object, List<ItemAttribute>> itemAttributes = new HashMap<>();

        if(catalogDomainServiceConfiguration.getSyntheticGroups().get(group) != null) {
            Set<Object[]> keys = new HashSet<>();
            int groupSize = catalogDomainServiceConfiguration.getSyntheticGroups().get(group).size();
            String clusteringColumns[][] = new String[groupSize][1];
            for (int i = 0; i < groupSize; i++) {
                clusteringColumns[i][0] = catalogDomainServiceConfiguration.getSyntheticGroups().get(group).get(i);
            }
            for(Long itemId : itemIds) {
                keys.add(new Object[]{itemId, clusteringColumns});
            }

            itemAttributes = catalogDaoProvider.getDAO(ItemAttribute.class).get(keys, itemAttributeReadOptions);

        } else {
            List<ItemAttribute> keys = itemIds.stream().map(ItemAttribute::new).collect(Collectors.toList());
            Map<Object, List<ItemAttribute>> unfilteredItemAttributes = catalogDaoProvider.getDAO(ItemAttribute.class).get(keys, itemAttributeReadOptions);
            for (Map.Entry<Object, List<ItemAttribute>> entry : unfilteredItemAttributes.entrySet()) {
                List<ItemAttribute> filteredAttributes = entry.getValue().stream().filter(itemAttribute ->
                        (itemAttribute.getGroup() != null && itemAttribute.getGroup().equals(group))).collect(Collectors.toList());
                if(filteredAttributes != null && filteredAttributes.size() > 0) {
                    itemAttributes.put(entry.getValue().get(0).getItemId(), filteredAttributes);
                }
            }
        }
        return itemAttributes;
    }

    @Override
    public List<ItemListing> getItemListings(List<Long> itemIds, Integer store, Integer limit) {
        validateListingItems(itemIds);
        validateStore(store);

        List<ItemListing> itemListDOs = new ArrayList<>();
        for (Long itemId : itemIds) {
            ItemListing itemList = new ItemListing();
            itemList.setItemId(itemId);
            itemList.setStore(store);
            itemListDOs.add(itemList);
        }
        Map<Object, List<ItemListing>> itemListings = catalogDaoProvider.getDAO(ItemListing.class).get(itemListDOs, getItemListingReadOptions(limit));

        if (itemListings != null && !itemListings.isEmpty()) {
            List<ItemListing> results = new ArrayList<>();
            for (Map.Entry<Object, List<ItemListing>> entry : itemListings.entrySet()) {
                results.addAll(entry.getValue());
            }
            return results;
        }

        return null;
    }

    @Override
    public List<ItemListing> getItemListings(List<Long> itemIds, List<Integer> stores, Integer limit) {
        validateListingItems(itemIds);
        validateStores(stores);

        List<ItemListing> itemListDOs = new ArrayList<>();
        for (Long itemId : itemIds) {
            for (Integer storeId : stores) {
                ItemListing itemList = new ItemListing();
                itemList.setItemId(itemId);
                itemList.setStore(storeId);
                itemListDOs.add(itemList);
            }
        }

        Map<Object, List<ItemListing>> itemListings = catalogDaoProvider.getDAO(ItemListing.class).get(itemListDOs, getItemListingReadOptions(limit));

        if (itemListings != null && !itemListings.isEmpty()) {
            List<ItemListing> results = new ArrayList<>();
            for (Map.Entry<Object, List<ItemListing>> entry : itemListings.entrySet()) {
                results.addAll(entry.getValue());
            }
            return results;
        }

        return null;
    }


    @Override
    public List<ItemListing> getItemListings(Long itemId, Integer store, Integer limit) {
        return getItemListings(Arrays.asList(itemId), store, limit);
    }

    @Override
    public ItemListing getItemListing(Long itemId, Integer limit) {

        List<ItemListing> itemListings = catalogDaoProvider.getDAO(ItemListing.class).get(new Long[]{itemId}, getItemListingReadOptions(limit));

        if (itemListings == null || itemListings.isEmpty()) {
            throw new InvalidItemException();
        }

        return itemListings.get(0);
    }

    @Override
    public MVendorSkuItem getItemByVendorAndSku(Long mVendor, Long sku) {
        if (mVendor == null || sku == null) {
            throw new MissingItemException();
        }

        List<MVendorSkuItem> mVendorSkuItems = catalogDaoProvider.getDAO(MVendorSkuItem.class).get(new Long[]{mVendor, sku}, mVendorSkuReadOptions);

        if (mVendorSkuItems == null || mVendorSkuItems.isEmpty()) {
            throw new InvalidItemException();
        }

        return mVendorSkuItems.get(0);

    }

    private void validateListingItems(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            throw new MissingItemException();
        }

        if (itemIds.size() > itemListingMaxRequests) {
            throw new InvalidItemException();
        }

        //deduplicate the items
        itemIds = itemIds.stream().distinct().collect(Collectors.toCollection(ArrayList::new));

        for(Long itemId : itemIds) {
            if(itemId < itemIdMinimum || itemId > itemIdMaximum) {
                throw new InvalidItemException();
            }
        }

    }

    private void validateAttributeItems(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            throw new MissingItemException();
        }

        if (itemIds.size() > itemAttributeMaxRequests) {
            throw new InvalidItemException();
        }

        //deduplicate the items
        itemIds = itemIds.stream().distinct().collect(Collectors.toCollection(ArrayList::new));

        for(Long itemId : itemIds) {
            if(itemId < itemIdMinimum || itemId > itemIdMaximum) {
                throw new InvalidItemException();
            }
        }

    }

    private void validateStore(Integer storeId) {
        if (storeId == null) {
            throw new InvalidStoreException();
        }

        if (storeId < storeIdMinimum || storeId > storeIdMaximum) {
            throw new InvalidStoreException();
        }
    }

    private void validateStores(List<Integer> storeIds) {
        //validate limits
        if (storeIds == null || storeIds.isEmpty()) {
            throw new InvalidStoreException();
        }

        if (storeIds.size() > storeMaxRequests)  {
            throw new InvalidStoreException();
        }

        for(Integer storeId : storeIds) {
            if(storeId < storeIdMinimum || storeId > storeIdMaximum) {
                throw new InvalidStoreException();
            }
        }

    }

    private void validateByGuidRequest(List<Long> itemIds, List<String> guids) {
        if(itemIds.size() > itemAttributeByGuidsMaxIds) {
            throw new InvalidItemException();
        }

        if(guids.size() > itemAttributeByGuidsMaxGuids) {
            throw new InvalidGuidException();
        }
    }

    /**
     * Generates a dynamic read options for the itemlisting
     * @param limit the limit
     * @return the populated read options
     */
    private ReadOptions getItemListingReadOptions(Integer limit) {

        ReadOptions itemListingReadOptions;

        if (limit == null) {
            itemListingReadOptions = new ReadOptions();
        } else {
            if (limit < 1) {
                throw new InvalidLimitException();
            }
            itemListingReadOptions = new ReadOptions(limit);
        }

        itemListingReadOptions.addColumn(ItemListColumn.ITEM_ID);
        itemListingReadOptions.addColumn(ItemListColumn.STORE);
        itemListingReadOptions.addColumn(ItemListColumn.DT_START);
        itemListingReadOptions.addColumn(ItemListColumn.NAME);
        itemListingReadOptions.addColumn(ItemListColumn.BRAND);
        itemListingReadOptions.addColumn(ItemListColumn.MODEL);
        itemListingReadOptions.addColumn(ItemListColumn.TYPE);
        itemListingReadOptions.addColumn(ItemListColumn.IMAGE);
        itemListingReadOptions.addColumn(ItemListColumn.ELG_FREE_SHIP);
        itemListingReadOptions.addColumn(ItemListColumn.FREE_SHIP_ITEM);
        itemListingReadOptions.addColumn(ItemListColumn.THD_IN_STORE);
        itemListingReadOptions.addColumn(ItemListColumn.SAVING_CENTER);
        itemListingReadOptions.addColumn(ItemListColumn.HIDE_SKU);
        itemListingReadOptions.addColumn(ItemListColumn.DISCONTINUED);
        itemListingReadOptions.addColumn(ItemListColumn.HAZARD_CODE);
        itemListingReadOptions.addColumn(ItemListColumn.HIDE_BRAND);
        itemListingReadOptions.addColumn(ItemListColumn.BOPIS);
        itemListingReadOptions.addColumn(ItemListColumn.BODFS);
        itemListingReadOptions.addColumn(ItemListColumn.BOSS);
        itemListingReadOptions.addColumn(ItemListColumn.HIDE_PRC);
        itemListingReadOptions.addColumn(ItemListColumn.SHIP_TYPE);
        itemListingReadOptions.addColumn(ItemListColumn.URL);
        itemListingReadOptions.addColumn(ItemListColumn.REVIEW);
        itemListingReadOptions.addColumn(ItemListColumn.RATING);
        itemListingReadOptions.addColumn(ItemListColumn.ECO_REBATE);
        itemListingReadOptions.addColumn(ItemListColumn.SSK_MN_PRC);
        itemListingReadOptions.addColumn(ItemListColumn.SSK_MX_PRC);
        itemListingReadOptions.addColumn(ItemListColumn.SSK_NM);
        itemListingReadOptions.addColumn(ItemListColumn.SSK_IMG);
        itemListingReadOptions.addColumn(ItemListColumn.SSK_CNT);
        itemListingReadOptions.addColumn(ItemListColumn.SSK_SWCH_GRP);
        itemListingReadOptions.addColumn(ItemListColumn.INVENTORY);
        itemListingReadOptions.addColumn(ItemListColumn.PRICE);
        itemListingReadOptions.addColumn(ItemListColumn.DT_END);
        itemListingReadOptions.addColumn(ItemListColumn.DESC_LONG);
        itemListingReadOptions.addColumn(ItemListColumn.DESC_SHORT);
        itemListingReadOptions.addColumn(ItemListColumn.WAS1);
        itemListingReadOptions.addColumn(ItemListColumn.WAS2);
        itemListingReadOptions.addColumn(ItemListColumn.STATUS);
        itemListingReadOptions.addColumn(ItemListColumn.MAPRICE);
        itemListingReadOptions.addColumn(ItemListColumn.BLKPRICE);
        itemListingReadOptions.addColumn(ItemListColumn.SELLUOM);
        itemListingReadOptions.addColumn(ItemListColumn.SQFTPERCASE);
        itemListingReadOptions.addColumn(ItemListColumn.THD_ONLINESTATUS);
        itemListingReadOptions.addColumn(ItemListColumn.STORE_SKU);
        itemListingReadOptions.addColumn(ItemListColumn.SURE_FIT);
        itemListingReadOptions.addColumn(ItemListColumn.STH_ELIGIBLE);
        itemListingReadOptions.addColumn(ItemListColumn.PURCHASE_LIMIT);
        itemListingReadOptions.addColumn(ItemListColumn.REPLACEMENT_ID);
        itemListingReadOptions.addColumn(ItemListColumn.ALTERNATE_PRICE);
        itemListingReadOptions.addColumn(ItemListColumn.BACKORDER);
        itemListingReadOptions.addColumn(ItemListColumn.PROMO_ID);
        return itemListingReadOptions;
    }
}
