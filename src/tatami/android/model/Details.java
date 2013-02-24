package tatami.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>
 * {@link Details} object creates 1 status and N statuses
 * </p>
 * 
 * @author pariviere
 */
@DatabaseTable(tableName = "details")
public class Details {
	@DatabaseField(columnName = "_id", generatedId = true)
	private long id;

	@DatabaseField(canBeNull = false, index = true)
	private String detailsId;

	@DatabaseField(canBeNull = false, index = true)
	private String statusId;
	
	public void setDetailsId(String detailsId) {
		this.detailsId = detailsId;
	}
	
	public String getDetailsId() {
		return detailsId;
	}
	
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	
	public String getStatusId() {
		return statusId;
	}
}
