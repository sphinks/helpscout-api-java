package net.helpscout.api;

import net.helpscout.api.model.*;
import net.helpscout.api.model.customer.SearchCustomer;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * @Author: ivan
 * Date: 03.09.16
 * Time: 14:03
 */
public interface ApiDeclaration {

    //    @Headers({
    //            "Content-Type: application/json;charset=utf-8",
    //            "Accept: application/json"
    //    })
    @GET("customers.json")
    Call<Page<Customer>> getCustomers(@Query("page") Integer page, @QueryMap Map<String, String> searchParams, @Query("fields") String... fields);

    @GET("customers/{customerId}.json")
    Call<ItemWrapper<Customer>> getCustomer(@Path("customerId") Long customerId, @Query("fields") String... fields);

    @GET("search/customers.json")
    Call<Page<SearchCustomer>> searchCustomers(@QueryMap Map<String, String> searchParams);

    @GET("mailboxes/{mailboxId}/customers.json")
    Call<Page<Customer>> getCustomersForMailbox(@Path("mailboxId") Long mailboxId, @Query("page") Integer page, @Query("fields") String... fields);

    @GET("users.json")
    Call<Page<MailboxUser>> getUsers(@QueryMap Map<String, String> searchParams, @Query("fields") String... fields);

    @GET("users/me.json")
    Call<ItemWrapper<User>> getUserMe();

    @GET("users/{userId}.json")
    Call<ItemWrapper<MailboxUser>> getUser(@Path("userId") Long userId, @Query("fields") String... fields);

    @GET("teams.json")
    Call<Page<Team>> getTeams(@QueryMap Map<String, String> searchParams, @Query("fields") String... fields);

    @GET("teams/{teamId}.json")
    Call<ItemWrapper<Team>> getTeam(@Path("teamId") Long teamId, @Query("fields") String... fields);
}
