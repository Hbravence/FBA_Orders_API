package com.fba.orders;
import java.util.*;
import io.swagger.client.model.GetOrderMetricsResponse;
import io.swagger.client.api.SalesApi;
import io.swagger.client.ApiException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.amazon.SellingPartnerAPIAA.LWAException;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import static com.amazon.SellingPartnerAPIAA.ScopeConstants.SCOPE_NOTIFICATIONS_API;
import static com.amazon.SellingPartnerAPIAA.ScopeConstants.SCOPE_MIGRATION_API;


/**
 * Hello world!
 *
 */
public class Handler implements RequestHandler<ScheduledEvent, String>
{
    static List<String> marketplaceIds = Arrays.asList("ATVPDKIKX0DER");
    static String granularity = "DAY";
    static String period = "P1D";
    static String buyerType = "All";
    static String fulfillmentNetwork = "";
    static String firstDayOfWeek = "Monday";
    static String asin = "";
    static String sku = "";
    static String state = "Active";
    static String interval = "2021-01-01T00:00:00Z/2022-01-31T23:59:59Z";

    @Override
    public String handleRequest(ScheduledEvent event, Context context)
    {

        LambdaLogger logger = context.getLogger();
        logger.log("EVENT: " + event);

        //API call
        LWAAuthorizationCredentials lwaAuthorizationCredentials =
        LWAAuthorizationCredentials.builder()
        .clientId("amzn1.application-oa2-client.0axxxxxxxxxxxxxxxxxx7698c35e5d66")
        .clientSecret("amzn1.oa2-cs.v1.3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxe055a")
        .withScopes(SCOPE_NOTIFICATIONS_API, SCOPE_MIGRATION_API)
        .endpoint("https://api.amazon.com/auth/o2/token")
        .build();
        SalesApi salesAPi = new SalesApi.Builder().lwaAuthorizationCredentials(lwaAuthorizationCredentials)
        .endpoint("https://sandbox.sellingpartnerapi-na.amazon.com").build();
        
        try {
            // Replace GetOrderMetricsResponse.() with a valid method call or variable name
            // Replace getOrderMetricsResponse() with a valid method call or variable name
            // For example, you can replace it with salesAPi.getOrderMetrics()
    
            GetOrderMetricsResponse getOrderMetricsResponse = salesAPi.getOrderMetrics(marketplaceIds, granularity, period, buyerType, fulfillmentNetwork, firstDayOfWeek, asin, sku, state);
            Region region = Region.US_EAST_1;
            S3Client s3 = S3Client.builder().region(region).build();
            String bucket = "orders-for-fba-sellers";
            String key = "125b99fd-0fd1-456f-b969-a48a7c5e7b9d";
            String content = getOrderMetricsResponse.toString();
            PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
            RequestBody requestBody = RequestBody.fromString(content);
            s3.putObject(putOb, requestBody);
            s3.close();
            System.out.println("Done");
        } catch (ApiException e) {
            System.err.println("Exception when calling SellersApi#getMarketplaceParticipations");
            e.printStackTrace();
        }
        catch (LWAException e) {
            System.err.println("LWA Exception when calling SellersApi#getMarketplaceParticipations");
            System.err.println(e.getErrorCode());
            System.err.println(e.getErrorMessage());
            e.printStackTrace();
        }

        return "Success";
    }
    }
 