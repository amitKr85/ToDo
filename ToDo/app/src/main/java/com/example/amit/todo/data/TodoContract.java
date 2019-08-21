package com.example.amit.todo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TodoContract {

    /**
     * constants for uri's
     */
    public static final String CONTENT_AUTHORITY="com.example.amit.todo";

    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_TODO="todo";

    private TodoContract(){}

    /**
     * class for table todo
     */
    public static final class TodoEntry implements BaseColumns{

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        /**
         * content uri
         */
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TODO);
        /**
         * Column constants and table constant
         */
        public static final String TABLE_NAME="todo";

        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_TASK ="task";
        public static final String COLUMN_PRIORITY ="priority";
        public static final String COLUMN_DONE ="done";

        /**
         * Priority Constants
         */
        public static final int DO_IF_POSSIBLE=1;
        public static final int SHOULD_DO=2;
        public static final int NORMAL=3;
        public static final int IMPORTANT=4;
        public static final int VERY_IMPORTANT=5;

        /**
         * contants for boolean
         */
        public static final int TRUE=1;
        public static final int FALSE=0;

        /**
         * checks for valid given parameter
         * @param priority
         * @return
         */
        public static boolean isValidPriority(int priority){
            if(priority >= DO_IF_POSSIBLE && priority <=VERY_IMPORTANT)
                return true;
            else
                return false;
        }

        /**
         * check for valid done value
         */
        public static final boolean isValidDone(int i){
            if(i==0 || i==1)
                return true;
            else
                return false;
        }
        /**
         * return boolean equiv. to integer
         */
        public static boolean getEquivalentBoolean(int i){
            if(i==TRUE)
                return true;
            else if(i==FALSE)
                return false;
            else
                throw new IllegalArgumentException("cant match given integer to any boolean");
        }
    }
}
