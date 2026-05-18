package com.neverforget.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.neverforget.data.local.entity.SummaryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SummaryDao_Impl implements SummaryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SummaryEntity> __insertionAdapterOfSummaryEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsRead;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SummaryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSummaryEntity = new EntityInsertionAdapter<SummaryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `summaries` (`id`,`title`,`summary_content`,`original_message_ids`,`source_app`,`conversation_name`,`date_range`,`is_read`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SummaryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getSummaryContent());
        statement.bindString(4, entity.getOriginalMessageIds());
        statement.bindString(5, entity.getSourceApp());
        statement.bindString(6, entity.getConversationName());
        statement.bindString(7, entity.getDateRange());
        final int _tmp = entity.isRead() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfMarkAsRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE summaries SET is_read = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM summaries";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SummaryEntity summary, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSummaryEntity.insertAndReturnId(summary);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsRead(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsRead.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkAsRead.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SummaryEntity>> getAllSummaries() {
    final String _sql = "SELECT * FROM summaries ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"summaries"}, new Callable<List<SummaryEntity>>() {
      @Override
      @NonNull
      public List<SummaryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSummaryContent = CursorUtil.getColumnIndexOrThrow(_cursor, "summary_content");
          final int _cursorIndexOfOriginalMessageIds = CursorUtil.getColumnIndexOrThrow(_cursor, "original_message_ids");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "source_app");
          final int _cursorIndexOfConversationName = CursorUtil.getColumnIndexOrThrow(_cursor, "conversation_name");
          final int _cursorIndexOfDateRange = CursorUtil.getColumnIndexOrThrow(_cursor, "date_range");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "is_read");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<SummaryEntity> _result = new ArrayList<SummaryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SummaryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSummaryContent;
            _tmpSummaryContent = _cursor.getString(_cursorIndexOfSummaryContent);
            final String _tmpOriginalMessageIds;
            _tmpOriginalMessageIds = _cursor.getString(_cursorIndexOfOriginalMessageIds);
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpConversationName;
            _tmpConversationName = _cursor.getString(_cursorIndexOfConversationName);
            final String _tmpDateRange;
            _tmpDateRange = _cursor.getString(_cursorIndexOfDateRange);
            final boolean _tmpIsRead;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SummaryEntity(_tmpId,_tmpTitle,_tmpSummaryContent,_tmpOriginalMessageIds,_tmpSourceApp,_tmpConversationName,_tmpDateRange,_tmpIsRead,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSummaryById(final long id,
      final Continuation<? super SummaryEntity> $completion) {
    final String _sql = "SELECT * FROM summaries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SummaryEntity>() {
      @Override
      @Nullable
      public SummaryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSummaryContent = CursorUtil.getColumnIndexOrThrow(_cursor, "summary_content");
          final int _cursorIndexOfOriginalMessageIds = CursorUtil.getColumnIndexOrThrow(_cursor, "original_message_ids");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "source_app");
          final int _cursorIndexOfConversationName = CursorUtil.getColumnIndexOrThrow(_cursor, "conversation_name");
          final int _cursorIndexOfDateRange = CursorUtil.getColumnIndexOrThrow(_cursor, "date_range");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "is_read");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final SummaryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSummaryContent;
            _tmpSummaryContent = _cursor.getString(_cursorIndexOfSummaryContent);
            final String _tmpOriginalMessageIds;
            _tmpOriginalMessageIds = _cursor.getString(_cursorIndexOfOriginalMessageIds);
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpConversationName;
            _tmpConversationName = _cursor.getString(_cursorIndexOfConversationName);
            final String _tmpDateRange;
            _tmpDateRange = _cursor.getString(_cursorIndexOfDateRange);
            final boolean _tmpIsRead;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new SummaryEntity(_tmpId,_tmpTitle,_tmpSummaryContent,_tmpOriginalMessageIds,_tmpSourceApp,_tmpConversationName,_tmpDateRange,_tmpIsRead,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SummaryEntity>> getSummariesByApp(final String sourceApp) {
    final String _sql = "SELECT * FROM summaries WHERE source_app = ? ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sourceApp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"summaries"}, new Callable<List<SummaryEntity>>() {
      @Override
      @NonNull
      public List<SummaryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSummaryContent = CursorUtil.getColumnIndexOrThrow(_cursor, "summary_content");
          final int _cursorIndexOfOriginalMessageIds = CursorUtil.getColumnIndexOrThrow(_cursor, "original_message_ids");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "source_app");
          final int _cursorIndexOfConversationName = CursorUtil.getColumnIndexOrThrow(_cursor, "conversation_name");
          final int _cursorIndexOfDateRange = CursorUtil.getColumnIndexOrThrow(_cursor, "date_range");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "is_read");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<SummaryEntity> _result = new ArrayList<SummaryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SummaryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSummaryContent;
            _tmpSummaryContent = _cursor.getString(_cursorIndexOfSummaryContent);
            final String _tmpOriginalMessageIds;
            _tmpOriginalMessageIds = _cursor.getString(_cursorIndexOfOriginalMessageIds);
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpConversationName;
            _tmpConversationName = _cursor.getString(_cursorIndexOfConversationName);
            final String _tmpDateRange;
            _tmpDateRange = _cursor.getString(_cursorIndexOfDateRange);
            final boolean _tmpIsRead;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SummaryEntity(_tmpId,_tmpTitle,_tmpSummaryContent,_tmpOriginalMessageIds,_tmpSourceApp,_tmpConversationName,_tmpDateRange,_tmpIsRead,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getUnreadCount() {
    final String _sql = "SELECT COUNT(*) FROM summaries WHERE is_read = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"summaries"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
