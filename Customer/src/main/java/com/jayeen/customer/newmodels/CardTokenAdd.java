package com.jayeen.customer.newmodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AndroidDev-Kalai on 12/27/2016.
 */

public class CardTokenAdd implements Serializable {

    @SerializedName("success")
    public Boolean success;

    @SerializedName("error_code")
    public Integer error_code;

    @SerializedName("error")
    public String error;

    @SerializedName("id")
    public Integer id;

    @SerializedName("owner_id")
    public Integer ownerId;

    @SerializedName("customer_id")
    public Integer customerId;

    @SerializedName("last_four")
    public String lastFour;

    @SerializedName("card_token")
    public String cardToken;

    @SerializedName("card_type")
    public String cardType;

    @SerializedName("card_id")
    public String cardId;

    @SerializedName("is_default")
    public Boolean isDefault;

    public class Error {

        @SerializedName("httpStatus")
        
        public Integer httpStatus;
        @SerializedName("httpBody")
        
        public String httpBody;
        @SerializedName("jsonBody")
        
        public JsonBody jsonBody;
        @SerializedName("httpHeaders")
        
        public HttpHeaders httpHeaders;
        @SerializedName("requestId")
        
        public String requestId;
        @SerializedName("stripeParam")
        
        public String stripeParam;
        @SerializedName("stripeCode")
        
        public String stripeCode;
        @SerializedName("declineCode")
        
        public String declineCode;
        @SerializedName("xdebug_message")
        
        public String xdebugMessage;

    }

    public class HttpHeaders {

        @SerializedName("Server")
        
        public String server;
        @SerializedName("Date")
        
        public String date;
        @SerializedName("Content-Type")
        
        public String contentType;
        @SerializedName("Content-Length")
        
        public String contentLength;
        @SerializedName("Connection")
        
        public String connection;
        @SerializedName("Access-Control-Allow-Credentials")
        
        public String accessControlAllowCredentials;
        @SerializedName("Access-Control-Allow-Methods")
        
        public String accessControlAllowMethods;
        @SerializedName("Access-Control-Allow-Origin")
        
        public String accessControlAllowOrigin;
        @SerializedName("Access-Control-Max-Age")
        
        public String accessControlMaxAge;
        @SerializedName("Cache-Control")
        
        public String cacheControl;
        @SerializedName("Request-Id")
        
        public String requestId;
        @SerializedName("Stripe-Version")
        
        public String stripeVersion;

    }
    public class JsonBody {

        @SerializedName("error")
        
        public JsonError error;

    }
    public class JsonError {

        @SerializedName("message")
        
        public String message;
        @SerializedName("type")
        
        public String type;
        @SerializedName("param")
        
        public String param;
        @SerializedName("code")
        
        public String code;
        @SerializedName("decline_code")
        
        public String declineCode;

    }

}
