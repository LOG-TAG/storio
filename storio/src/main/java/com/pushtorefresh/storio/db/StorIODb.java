package com.pushtorefresh.storio.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.db.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.storio.db.operation.get.PreparedGet;
import com.pushtorefresh.storio.db.operation.put.PreparedPut;
import com.pushtorefresh.storio.db.query.DeleteQuery;
import com.pushtorefresh.storio.db.query.InsertQuery;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.db.query.UpdateQuery;

import java.util.Collections;
import java.util.Set;

import rx.Observable;

/**
 * Powerful abstraction for databases
 * <p/>
 * It's an abstract class instead of interface because we want to have ability to add some
 * changes without breaking existing implementations
 */
public abstract class StorIODb {

    /**
     * Prepares "execute sql" operation for {@link StorIODb}
     * Allows to execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE.
     *
     * @return builder for PreparedExecSql
     */
    @NonNull public PreparedExecSql.Builder execSql() {
        return new PreparedExecSql.Builder(this);
    }

    /**
     * Prepares "get" operation for {@link StorIODb}
     * Allows to get information from {@link StorIODb}
     *
     * @return builder for PreparedGet
     */
    @NonNull public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    /**
     * Prepares "put" operation for {@link StorIODb}
     * Allows to insert/update information in {@link StorIODb}
     *
     * @return builder for PreparedPut
     */
    @NonNull public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    /**
     * Prepares "delete" operation for {@link StorIODb}
     * Allows to delete information from {@link StorIODb}
     *
     * @return builder for PreparedDelete
     */
    @NonNull public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    /**
     * Subscribes to changes in required tables
     *
     * @param tables set of table names that should be monitored
     * @return {@link rx.Observable} of {@link Changes} subscribed to changes in required tables
     */
    @NonNull
    public abstract Observable<Changes> observeChangesInTables(@NonNull Set<String> tables);

    /**
     * Subscribes to changes in required table
     *
     * @param table table name to monitor
     * @return {@link rx.Observable} of {@link Changes} subscribed to changes in required table
     */
    @NonNull
    public Observable<Changes> observeChangesInTable(@NonNull String table) {
        return observeChangesInTables(Collections.singleton(table));
    }

    /**
     * Hides some internal operations for {@link StorIODb} to make API of {@link StorIODb} clean and easy to understand
     *
     * @return implementation of Internal operations for {@link StorIODb}
     */
    @NonNull public abstract Internal internal();

    /**
     * Hides some internal operations of {@link StorIODb}
     * to make {@link StorIODb} API clean and easy to understand
     */
    public static abstract class Internal {

        /**
         * Execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE on the database
         *
         * @param rawQuery sql query
         */
        public abstract void execSql(@NonNull RawQuery rawQuery);

        /**
         * Executes raw query on the database and returns {@link android.database.Cursor} over the result set
         *
         * @param rawQuery sql query
         * @return A Cursor object, which is positioned before the first entry. Note that Cursors are not synchronized, see the documentation for more details.
         */
        @NonNull public abstract Cursor rawQuery(@NonNull RawQuery rawQuery);

        /**
         * Executes query on the database and returns {@link android.database.Cursor} over the result set
         *
         * @param query sql query
         * @return A Cursor object, which is positioned before the first entry. Note that Cursors are not synchronized, see the documentation for more details.
         */
        @NonNull public abstract Cursor query(@NonNull Query query);

        /**
         * Inserts a row into the database
         *
         * @param insertQuery   query
         * @param contentValues map that contains the initial column values for the row. The keys should be the column names and the values the column values
         * @return id of inserted row
         */
        public abstract long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        /**
         * Updates one or multiple rows in the database
         *
         * @param updateQuery   query
         * @param contentValues a map from column names to new column values. null is a valid value that will be translated to NULL.
         * @return the number of rows affected
         */
        public abstract int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        /**
         * Deletes one or multiple rows in the database
         *
         * @param deleteQuery query
         * @return the number of rows deleted
         */
        public abstract int delete(@NonNull DeleteQuery deleteQuery);

        /**
         * Notifies subscribers about changes happened in {@link StorIODb}
         * Operations can be executed in transaction or one operation can affect multiple tables, so to reduce number of notifications
         * you can call this method once and provide Changes object
         *
         * @param changes changes happened in {@link StorIODb}
         */
        public abstract void notifyAboutChanges(@NonNull Changes changes);

        /**
         * Returns true if {@link StorIODb} implementation supports transactions
         *
         * @return true if transactions are supported, false otherwise
         */
        public abstract boolean transactionsSupported();

        /**
         * Begins a transaction in EXCLUSIVE mode
         */
        public abstract void beginTransaction();

        /**
         * Marks the current transaction as successful
         */
        public abstract void setTransactionSuccessful();

        /**
         * End a transaction
         */
        public abstract void endTransaction();
    }
}
