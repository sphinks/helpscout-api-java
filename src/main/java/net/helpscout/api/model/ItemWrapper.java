package net.helpscout.api.model;

import lombok.Data;

/**
 * @Author: ivan
 * Date: 18.09.16
 * Time: 12:02
 */
@Data
public class ItemWrapper<T> {

    private T item;
}
