package com.example.mercadolibrechallenge.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This class is called from every activity to pass to another one
 */
public class NavigationHelper {
    /**
     *
     * @param context Activity context when is called
     * @param target Activity class to go.
     * @param productId Used from SearchList to ProductDetail; when is called from Home, this value is empty.
     * @param stringSearch Becomes from home and is passed always between activities until come back to home.
     * @param limit Is the limit of objects that App get from Mercado Libre Api. In Home class has been set as 10 by default
     * @param offset Start point (index) on products array that App get from Mercado Libre Api. In Home class has been set as 0 by default
     * @param page Current search page of SearchList. In Home class has been set as 1 by default
     */
    public static void navigateTo(Context context, Class target, String productId, String stringSearch, int limit, int offset, int page) {
        Intent goToTarget = new Intent(context, target);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("productId", productId);
        dataBundle.putString("stringSearch", stringSearch);
        dataBundle.putInt("limit", limit);
        dataBundle.putInt("offset", offset);
        dataBundle.putInt("page", page);
        goToTarget.putExtras(dataBundle);
        goToTarget.addFlags(goToTarget.FLAG_ACTIVITY_CLEAR_TOP | goToTarget.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(goToTarget);
    }
}
