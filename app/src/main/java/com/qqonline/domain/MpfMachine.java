package com.qqonline.domain;

public class MpfMachine {
	private int ID; 
	private int DbId;
	private String MachineCode;
	private String MachineSerialNumber;
	private String BindingPassword;
	public int getID() {
		return ID;
	}
	public int getDbId() {
		return DbId;
	}
	public void setDbId(int dbId) {
		DbId = dbId;
	}
	public String getMachineCode() {
		return MachineCode;
	}
	public void setMachineCode(String machineCode) {
		MachineCode = machineCode;
	}
	public String getMachineSerialNumber() {
		return MachineSerialNumber;
	}
	public void setMachineSerialNumber(String machineSerialNumber) {
		MachineSerialNumber = machineSerialNumber;
	}
	public String getBindingPassword() {
		return BindingPassword;
	}
	public void setBindingPassword(String bindingPassword) {
		BindingPassword = bindingPassword;
	}
}
