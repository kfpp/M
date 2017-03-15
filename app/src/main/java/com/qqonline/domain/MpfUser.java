package com.qqonline.domain;

import java.util.Date;

/**
 * ID,DbId,OpenId,Name,AddTime,FirstPicture
 * @author fengcheng.ye 
 *
 */
public class MpfUser {
	private int ID;
	private int DbId; 
	private String OpenId;
	private String Name;
	private Date AddTime;
	private MpfPicture FirstPicture;
	public int getDbId() {
		return DbId;
	}
	public void setDbId(int dbId) {
		DbId = dbId;
	}
	public String getOpenId() {
		return OpenId;
	}
	public void setOpenId(String openId) {
		OpenId = openId;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public Date getAddTime() {
		return AddTime;
	}
	public void setAddTime(Date addTime) {
		AddTime = addTime;
	}
	public int getID() {
		return ID;
	}
	public MpfPicture getFirstPicture() {
		return FirstPicture;
	}
	public void setFirstPicture(MpfPicture firstPicture) {
		FirstPicture = firstPicture;
	}
	
}
