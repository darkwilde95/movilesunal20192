package com.example.directory.data;

import android.provider.BaseColumns;

public final class DirectoryContract {

    private DirectoryContract() {}

    public static final class EnterpriseEntry implements BaseColumns {

        public static final String TABLE_NAME = "enterprises";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ENTERPRISE_NAME = "enterprise_name";
        public static final String COLUMN_WEB_SITE = "web_site";
        public static final String COLUMN_CONTACT_PHONE = "contact_phone";
        public static final String COLUMN_CONTACT_EMAIL = "contact_email";
        public static final String COLUMN_PRODUCTS_SERVICES = "products_services";
    }

    public static final class ClassificationsEnterprise implements BaseColumns {

        public static final String TABLE_NAME = "clasifications_enterprise";

        public static final String COLUMN_ENTERPRISE_ID = "enterprise_id";
        public static final String COLUMN_CLASSIFICATION_ID = "clasification_id";

        public static final int CONSULTORY_ID = 0;
        public static final int DEVELOPMENT_ID = 1;
        public static final int FACTORY_ID = 2;

    }
}
