package com.jayeen.customer.utils;

/**
 * @author Hardik A Bhalodi
 */
public class Const {
    public static final String TAG = "AUTOMATED TAXI";
    public static String COUNTRY_CODE="DO";
    public static final String PLACES_AUTOCOMPLETE_API_KEY = "AIzaSyAXatDcDqW3hERFA-zpeX86juvvWQr8ycM";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final int MAP_ZOOM = 20;
    // card io
    public static final String PUBLISHABLE_KEY = "pk_test_32zokmkeyujygxDUl2sQ44Ds";
    public static final String MY_CARDIO_APP_TOKEN = "c15fa417f757415c9d750d1ef5ee5fd8";
    // Your Facebook APP ID
    public static final String APP_ID = "1725943130955611"; // Replace with your App ID

    // web services
    public class ServiceType {
        private static final String HOST_URL2 = "http://productstaging.in/jayeentaxi/";
        //        private static final String HOST_URL2 = "http://192.168.1.25/jayeentaxi/";
//        private static final String HOST_URL2 = "http://192.168.1.25/jayeentaxi/";
//        private static final String HOST_URL2 = "http://notanotherfruit.com/jayeentaxi/";
        private static final String HOST_URL = HOST_URL2 + "public/";
        private static final String BASE_URL = HOST_URL + "user/";
        public static final String LOGIN = BASE_URL + "login";
        public static final String REGISTER = BASE_URL + "register";
        public static final String ADD_CARD = BASE_URL + "addcardtoken";
        public static final String ERASE_URL = HOST_URL + "/user/temp_user";
        public static final String CREATE_REQUEST = BASE_URL + "createrequest";
        public static final String CREATE_RIDELATERREQUEST = HOST_URL + "dog/addschedule";
        public static final String SCHEDULEHISTORY = HOST_URL + "dog/getUserSchedules";
        public static final String GET_REQUEST_LOCATION = BASE_URL + "getrequestlocation?";
        public static final String GET_REQUEST_STATUS = BASE_URL + "getrequest?";
        public static final String GET_ALLPROVIDER_LIST = BASE_URL + "allprovider_list";
        public static final String REGISTER_MYTHING = BASE_URL + "thing?";
        public static final String REQUEST_IN_PROGRESS = BASE_URL + "requestinprogress?";
        public static final String RATING = BASE_URL + "rating";
        public static final String SOSDETAIL = BASE_URL + "rating";
        public static final String CANCEL_REQUEST = BASE_URL + "cancelrequest";
        public static final String SET_DESTINATION = BASE_URL + "setdestination";
        public static final String GET_PAGES = HOST_URL + "application/pages";
        public static final String GET_PAGES_DETAIL = HOST_URL + "application/page/";
        public static final String GET_VEHICAL_TYPES = HOST_URL + "application/zonetypes";      // navi
        public static final String FORGET_PASSWORD = HOST_URL + "application/forgot-password";
        public static final String UPDATE_PROFILE = BASE_URL + "update";
        public static final String GET_CARDS = BASE_URL + "cards?";
        public static final String HISTORY = BASE_URL + "history?";
        public static final String GET_PATH = BASE_URL + "requestpath?";
        public static final String GET_REFERRAL = BASE_URL + "referral?";
        public static final String APPLY_REFFRAL_CODE = BASE_URL + "apply-referral";
        public static final String APPLY_OTP_CODE = BASE_URL + "checkotp";
        public static final String RESEND_OTP_CODE = BASE_URL + "resendotp";
        public static final String REMOVE_USER = BASE_URL + "temp_user";
        public static final String LATEST_GET_PROVIDERS = BASE_URL + "getprovidersall";
        public static final String PAYMENT_TYPE = BASE_URL + "payment_type";
        public static final String DEFAULT_CARD = BASE_URL + "card_selection";
        public static final String GET_WALLET_BALANCE = BASE_URL + "wallet_amount_details?";
        public static final String ADD_WALLET_AMOUNT = BASE_URL + "add_money_to_wallet";
        //        public static final String APPLY_PROMO = BASE_URL + "apply-promo";
        public static final String APPLY_PROMO = BASE_URL + "apply-promo_previously";
        public static final String LOGOUT = BASE_URL + "logout";
        public static final String SCHEDULECANCEL = HOST_URL + "dog/cancelschedule";
        public static final String DURATONAPI = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "units=imperial&origins=%s&destinations=%s&mode=driving&key=" + PLACES_AUTOCOMPLETE_API_KEY;
        public static final String GEOCODER_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&sensor=true&key=" + PLACES_AUTOCOMPLETE_API_KEY;
        public static final String STATIC_MAP_URL = "https://maps-api-ssl.google.com/maps/api/staticmap?size=%sx%s&scale=2&markers=shadow:true|scale:2|icon:http://d1a3f4spazzrp4.cloudfront.net/receipt-new/marker-start@2x.png|%s,%s&markers=shadow:false|scale:2|icon:http://d1a3f4spazzrp4.cloudfront.net/receipt-new/marker-finish@2x.png|%s,%s&path=color:0x2dbae4ff|weight:4|%s,%s|%s,%s";
        public static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/";
        public static final String GET_DURATION_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=%d,%d&destination=%d,%d";
        public static final String COUNTRY_CODE_URL = "https://freegeoip.net/json";
        public static final String GIVE_TIPS = BASE_URL + "tips";
        public static final String GET_BRAINTREE_TOKEN = HOST_URL + "token_braintree";
        public static final String ETA_CALC_URL = BASE_URL + "eta_calculation";
    }

    // prefname
    public static String PREF_NAME = "taxinowclient";
    // fragments tag
    public static String FRAGMENT_REGISTER = "FRAGMENT_REGISTER";
    public static String FRAGMENT_MAIN = "FRAGMENT_MAIN";
    public static String FRAGMENT_MYTHING_REGISTER = "FRAGMENT_MYTHING_REGISTER";
    public static String FRAGMENT_SIGNIN = "FRAGMENT_SIGNIN";
    public static String FRAGMENT_PAYMENT_REGISTER = "ADD_FRAGMENT_PAYMENT_REGISTER";
    public static String FRAGMENT_PAYMENT_ADD = "FRAGMENT_PAYMENT_ADD";
    public static String FRAGMENT_REFFREAL = "FRAGMENT_REFFREAL";
    public static String FRAGMENT_OTP = "FRAGMENT_OTP";
    public static String FRAGMENT_MYTHING_ADD = "FRAGMENT_MYTHING_ADD";
    public static String FRAGMENT_MAP = "FRAGMENT_MAP";
    public static String FRAGMENT_TRIP = "FRAGMENT_TRIP";
    public static String FRAGMENT_SOS = "FRAGMENT_SOS";
    public static final String FOREGETPASS_FRAGMENT_TAG = "FOEGETPASSFRAGMENT";
    public static String FRAGMENT_FEEDBACK = "FRAGMENT_FEEDBACK";
    public static String FRAGMENT_FULLDAY = "FRAGMENT_FULLDAY";
    public static String FRAGMENT_HALFDAY = "FRAGMENT_HALFDAY";
    public static String FRAGMENT_TOUR_DETAILS = "FRAGMENT_TOUR_DETAILS";

    // service codes
    public class ServiceCode {
        public static final int REGISTER = 1;
        public static final int LOGIN = 2;
        public static final int GET_ROUTE = 3;
        public static final int REGISTER_MYTHING = 4;
        public static final int ADD_WALLET_AMOUNT = 5;
        public static final int ADD_CARD = 6;
        public static final int WALLET_BALANCE = 7;
        public static final int CREATE_REQUEST = 8;
        public static final int GET_REQUEST_STATUS = 9;
        public static final int GET_REQUEST_LOCATION = 10;
        public static final int GET_REQUEST_IN_PROGRESS = 11;
        public static final int RATING = 12;
        public static final int CANCEL_REQUEST = 13;
        public static final int GET_PAGES = 14;
        public static final int GET_PAGES_DETAILS = 15;
        public static final int GET_VEHICAL_TYPES = 16;
        public static final int FORGET_PASSWORD = 18;
        public static final int UPDATE_PROFILE = 19;
        public static final int GET_CARDS = 20;
        public static final int HISTORY = 21;
        public static final int GET_PATH = 22;
        public static final int GET_REFERREL = 23;
        public static final int APPLY_REFFRAL_CODE = 24;
        public static final int BRAIN_TREE_CODE = 25;
        public static final int GET_PROVIDERS = 26;
        public static final int GET_DURATION = 27;
        public static final int DRAW_PATH_ROAD = 28;
        public static final int DRAW_PATH = 29;
        public static final int PAYMENT_TYPE = 30;
        public static final int DEFAULT_CARD = 31;
        public static final int GET_QUOTE = 32;
        public static final int GET_TOUR = 33;
        public static final int GET_FARE_QUOTE = 34;
        public static final int GET_NEAR_BY = 35;
        public static final int BOOK_TOUR = 36;
        public static final int SET_DESTINATION = 37;
        public static final int UPDATE_PROVIDERS = 38;
        public static final int APPLY_PROMO = 39;
        public static final int LOGOUT = 40;
        public static final int SOS = 41;
        public static final int GET_PROVIDER_LIST = 42;
        public static final int UPDATE_ALLPROVIDER_LIST = 43;
        public static final int APPLY_OTP_CODE = 44;
        public static final int CREATE_RIDELATERREQUEST = 45;
        public static final int SCHEDULEHISTORY = 46;
        public static final int REMOVEUSER = 47;
        public static final int RESEND_OTP_CODE = 48;
        public static final int SCHEDULECANCEL = 49;
        public static final int DURATIONREQUEST = 50;
        public static final int DRAW_PATHPICKUP = 51;
        public static final int LATEST_GET_PROVIDERS = 52;
        public static final int GIVE_TIPS = 54;
        public static final int GET_LATLONG = 55;
        public static final int GETDEST = 56;
        public static final int GETDEST_POSITION = 57;
        public static final int GETSOURCE_POSITION = 58;
        public static final int GET_FARE_EST = 59;
        public static final int BRAIN_TREE_CLINET_TOKEN = 60;
        public static final int BRAIN_TREE_DROP_IN = 61;
    }

    // service parameters
    public class Params {
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String INFORMATIONS = "informations";
        public static final String EMAIL = "email";
        public static final String CODE = "code";
        public static final String REFERRAL_CODE = "referral_code";
        public static final String OTP_CODE = "otpkey";
        public static final String SCHUSERID = "userId";
        public static final String USERID = "userid";
        public static final String OWNERID = "ownerid";
        public static final String PASSWORD = "password";
        public static final String OLD_PASSWORD = "old_password";
        public static final String NEW_PASSWORD = "new_password";
        public static final String FIRSTNAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String PHONE = "phone";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String ICON = "icon";
        public static final String DEVICE_TYPE = "device_type";
        public static final String LOCATION_DATA = "locationdata";
        public static final String BIO = "bio";
        public static final String ADDRESS = "address";
        public static final String STATE = "state";
        public static final String COUNTRY = "country";
        public static final String ZIPCODE = "zipcode";
        public static final String LOGIN_BY = "login_by";
        public static final String OTPTYPE = "otptype";
        public static final String ID = "id";
        public static final String TOKEN = "token";
        public static final String COMMENT = "comment";
        public static final String RATING = "rating";
        public static final String TAXI_MODEL = "car_model";
        public static final String TAXI_NUMBER = "car_number";
        public static final String SOCIAL_UNIQUE_ID = "social_unique_id";
        public static final String PICTURE = "picture";
        public static final String TYPE = "type";
        public static final String OWNER = "2";
        public static final String STRIPE_TOKEN = "payment_token";
        public static final String LAST_FOUR = "last_four";
        public static final String CARD_TYPE = "card_type";
        public static final String LONGITUDE = "longitude";
        public static final String BEARING = "bearing";
        public static final String LATITUDE = "latitude";
        public static final String DISTANCE = "distance";
        public static final String REQUEST_ID = "request_id";
        public static final String TIPS = "tips";
        public static final String REASONFORREJECT = "reason";
        public static final String DEST_LAT = "dest_lat";
        public static final String DEST_LNG = "dest_long";
        public static final String PAYMENT_OPT = "payment_opt";
        public static final String CARD_ID = "card_id";
        public static final String DESTINATION_LATITUDE1 = "d_latitude";
        public static final String DESTINATION_LONGITUDE1 = "d_longitude";
        public static final String CASH_OR_CARD = "cash_or_card";
        public static final String DEFAULT_CARD_ID = "default_card_id";
        public static final String IS_SKIP = "is_skip";
        public static final String IS_REFEREE = "is_referee";
        public static final String PROMO_CODE = "promo_code";
        public static final String TAXI_TYPE = "type";
        public static final String TAXI_DURATION = "duration";
        public static final String PICKUPLAT = "pickupLatitude";
        public static final String PICKUPLNG = "pickupLongitude";
        public static final String DROPOFFLAT = "dropoffLatitude";
        public static final String DROPOFFLNG = "dropoffLongitude";
        public static final String NOOFADULTS = "numberOfadults";
        public static final String NOOFCHILD = "noOfchildren";
        public static final String LUGGAGECOUNT = "luggageCount";
        public static final String RIDECOMMENT = "rideComment";
        public static final String TYPEOFCAR = "type";
        public static final String PICKUPLOC = "pickupLocation";
        public static final String PICKUPDET = "pickupDetails";
        public static final String DROPOFFLOC_RIDE_LATER = "dropoffLoc";
        public static final String DROPOFFDET = "dropoffDetails";
        public static final String SCHEDULETIME = "schedule_date";
        public static final String USER_TIMEZONE = "user_timezone";
        public static final String AMOUNT = "amount";
        public static final String STARTLONG = "start_long";
        public static final String ENDLAT = "end_lat";
        public static final String ENDLONG = "end_long";
        public static final String MAPURL = "map_url";
        public static final String PICKUP_ADDRESS = "pickupDetails";
        public static final String DROP_ADDRESS = "dropoffDetails";

        public static final String PICK_LAT = "pick_latitude";
        public static final String PICK_LONG = "pick_longitude";
        public static final String DROP_LAT = "drop_latitude";
        public static final String DROP_LONG = "drop_longitude";
    }

    // general
    public static final int CHOOSE_PHOTO = 112;
    public static final int TAKE_PHOTO = 113;
    public static final String URL = "url";
    public static final String DEVICE_TYPE_ANDROID = "android";
    public static final String SOCIAL_FACEBOOK = "facebook";
    public static final String SOCIAL_GOOGLE = "google";
    public static final String MANUAL = "manual";
    public static final String OTPTYPE = "true";

    // used for request status
    public static final int IS_WALKER_STARTED = 2;
    public static final int IS_WALKER_CANC = 7;
    public static final int IS_WALKER_ARRIVED = 3;
    public static final int IS_WALK_STARTED = 4;
    public static final int IS_COMPLETED = 5;
    public static final int IS_WALKER_RATED = 6;
    public static final int IS_REQEUST_CREATED = 1;

    // used for sending model in to bundle
    public static final String DRIVER = "driver";
    public static final String USER_DETAILS = "user_Details";
    public static final String THINGS = "things";
    // used for schedule request
    public static final long TIME_SCHEDULE = 10 * 1000;
    public static final long DELAY = 0 * 1000;

    // no request id
    public static final int NO_REQUEST = -1;
    public static final int NO_TIME = -1;

    // error code
    public static final int INVALID_TOKEN = 406;
    public static final int REQUEST_ID_NOT_FOUND = 408;
    public static final int REQUEST_ID_NOT_FOUND2 = 628;
    public static final int TOKEN_EXPIRED = 620;

    // notification
    public static final String INTENT_WALKER_STATUS = "walker_status";
    public static final String EXTRA_WALKER_STATUS = "walker_status_extra";

    // payment mode
    public static final int CASH = 1;
    public static final int CREDIT = 0;
    // Peach Payment
    public static final String APPLICATIONIDENTIFIER = "peach.unicab.mcommerce";
    public static final String PROFILETOKEN = "c60f25c78aa04a4baa80b3d627e1f0a5";
    // Card Type
    public static final String[] PREFIXES_AMERICAN_EXPRESS = {"34", "37"};
    public static final String[] PREFIXES_DISCOVER = {"60", "62", "64", "65"};
    public static final String[] PREFIXES_JCB = {"35"};
    public static final String[] PREFIXES_DINERS_CLUB = {"300", "301", "302", "303", "304", "305", "309", "36", "38", "37", "39"};
    public static final String[] PREFIXES_VISA = {"4"};
    public static final String[] PREFIXES_MASTERCARD = {"50", "51", "52", "53", "54", "55"};
    public static final String AMERICAN_EXPRESS = "American Express";
    public static final String DISCOVER = "Discover";
    public static final String JCB = "JCB";
    public static final String DINERS_CLUB = "Diners Club";
    public static final String VISA = "Visa";
    public static final String MASTERCARD = "MasterCard";
    public static final String UNKNOWN = "Unknown";
    // Tours type
    public static final int FULL_DAY_TOUR = 1;
    public static final int HALF_DAY_TOUR = 0;

    // Placesurls
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    public static final String TYPE_NEAR_BY = "/nearbysearch";
    public static final String OUT_JSON = "/json";
    public static final String CART_CHANGE_RECEIVER = "card_change_receiver";
    public static final String SUCCESS = "success";
}
