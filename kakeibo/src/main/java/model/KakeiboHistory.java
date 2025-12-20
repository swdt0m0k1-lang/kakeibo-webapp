package model;

import java.sql.Date;
import java.sql.Timestamp;

public class KakeiboHistory {

	private int id;
	private int kakeiboId;
	private int userId;

	private Date beforeDate;
	private Date afterDate;

	private String beforeType;
	private String afterType;

	private String beforeItem;
	private String afterItem;

	private int beforeAmount;
	private int afterAmount;

	private String beforeMemo;
	private String afterMemo;

	private Timestamp updatedAt;

	// 削除フラグ
	private boolean deleted = false;

	// --- getter / setter ---

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getKakeiboId() {
		return kakeiboId;
	}

	public void setKakeiboId(int kakeiboId) {
		this.kakeiboId = kakeiboId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(Date beforeDate) {
		this.beforeDate = beforeDate;
	}

	public Date getAfterDate() {
		return afterDate;
	}

	public void setAfterDate(Date afterDate) {
		this.afterDate = afterDate;
	}

	public String getBeforeType() {
		return beforeType;
	}

	public void setBeforeType(String beforeType) {
		this.beforeType = beforeType;
	}

	public String getAfterType() {
		return afterType;
	}

	public void setAfterType(String afterType) {
		this.afterType = afterType;
	}

	public String getBeforeItem() {
		return beforeItem;
	}

	public void setBeforeItem(String beforeItem) {
		this.beforeItem = beforeItem;
	}

	public String getAfterItem() {
		return afterItem;
	}

	public void setAfterItem(String afterItem) {
		this.afterItem = afterItem;
	}

	public int getBeforeAmount() {
		return beforeAmount;
	}

	public void setBeforeAmount(int beforeAmount) {
		this.beforeAmount = beforeAmount;
	}

	public int getAfterAmount() {
		return afterAmount;
	}

	public void setAfterAmount(int afterAmount) {
		this.afterAmount = afterAmount;
	}

	public String getBeforeMemo() {
		return beforeMemo;
	}

	public void setBeforeMemo(String beforeMemo) {
		this.beforeMemo = beforeMemo;
	}

	public String getAfterMemo() {
		return afterMemo;
	}

	public void setAfterMemo(String afterMemo) {
		this.afterMemo = afterMemo;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	// --- 削除フラグ用 getter / setter ---

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return afterDate == null;
	}

	public boolean isRestored() {
		return afterDate != null
				&& beforeDate != null
				&& beforeDate.equals(afterDate)
				&& safeEquals(beforeType, afterType)
				&& safeEquals(beforeItem, afterItem)
				&& beforeAmount == afterAmount
				&& safeEquals(beforeMemo, afterMemo);
	}

	private boolean safeEquals(String a, String b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return a.equals(b);
	}

}
