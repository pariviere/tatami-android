package tatami.android.content;

import android.provider.BaseColumns;

public class DbMeta {
	private DbMeta() {
	}

	public static final String DB_NAME = "tatami.db";
	public static final int DB_VERSION = 1;

	public class StatusTable implements BaseColumns {
		private StatusTable() {
		}

		public static final String SQL_QUERY_CREATE = 
				"CREATE TABLE "+ DbMeta.StatusTable.TABLE_NAME 
				+ "(" + DbMeta.StatusTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DbMeta.StatusTable.STATUS_ID + " TEXT NOT NULL, "
				+ DbMeta.StatusTable.CONTENT + " TEXT NOT NULL, ";
		public static final String TABLE_NAME = "status";
		public static final String ID = "_id";
		public static final String STATUS_ID = "statusId";
		public static final String CONTENT = "content";
		public static final String USERNAME = "username";
	}
}
