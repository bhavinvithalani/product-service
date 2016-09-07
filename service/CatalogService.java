package com.homedepot.catalog.service;

import com.homedepot.catalog.model.ItemAttribute;
import com.homedepot.catalog.model.ItemListing;
import com.homedepot.catalog.model.SuperSku;
import com.homedepot.catalog.model.MVendorSkuItem;

import java.util.List;
import java.util.Map;

/**
 * Class representing the catalog service interface
 */
public interface CatalogService {

    /**
     * Gets the item attributes by item id(s)
     *
     * @param itemIds     the list of item ids
     * @param guid        the optional guid
     * @param group       the optional group
     * @return the list of {@link ItemAttribute}
     */
    Map<Long, List<ItemAttribute>> getItemAttributes(List<Long> itemIds, String guid, String group);

    /**
     * Gets the item attributes by item id(s) and guid(s)
     *
     * @param itemIds    the list of item ids
     * @param guids      the list of guids
     * @return the list of {@link ItemAttribute}
     */
    Map<Long, List<ItemAttribute>> getItemAttributesByGuids(List<Long> itemIds, List<String> guids);

    /**
     * Gets the item listings by store
     *
     * @param itemIds the list of item ids
     * @param store   the store id
     * @param limit   the number of items (per item id) to return
     * @return the list of {@link ItemListing}
     */
    List<ItemListing> getItemListings(List<Long> itemIds, Integer store, Integer limit);

    /**
     * Gets the item listings by stores
     *
     * @param itemIds the list of item ids
     * @param store   the list of store ids
     * @param limit   the number of items (per item id) to return
     * @return the list of {@link ItemListing}
     */
    List<ItemListing> getItemListings(List<Long> itemIds, List<Integer> store, Integer limit);

    /**
     * Gets the first-found item listing, irrelevant of store
     *
     * @param itemId the item id
     * @param limit   the number of items (per item id) to return
     * @return the list of {@link ItemListing}
     */
    ItemListing getItemListing(Long itemId,  Integer limit);

    /**
     * Gets an item listing for a single store
     * @param itemId the item id
     * @param store the store id
     * @param limit   the number of items (per item id) to return
     * @return the list of {@link ItemListing}
     */
    List<ItemListing> getItemListings(Long itemId, Integer store, Integer limit);

    /**
     * Gets an item id by vendor and sku
     *
     * @param mVendor the vendor id
     * @param sku    the unique sku
     * @return a single {@link MVendorSkuItem}
     */
    MVendorSkuItem getItemByVendorAndSku(Long mVendor, Long sku);
}
