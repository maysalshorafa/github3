package com.pos.leaders.leaderspossystem.Models.Permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pos.leaders.leaderspossystem.Models.User;

import java.util.List;

/**
 * Created by KARAM on 29/10/2016.
 */

public class UserPermissions {
	//region Attribute
	private long userPermissionsId;
	private long userId;
	private long permissionId;
	@JsonIgnore
	private User user;
	@JsonIgnore
	private List<Permissions> permissions;

	//endregion

	//region Constructors

	public UserPermissions() {
	}

	public UserPermissions(long userPermissionsId, long userId, long permissionId) {
		this.userPermissionsId = userPermissionsId;
		this.userId = userId;
		this.permissionId = permissionId;
	}
	public UserPermissions(long permissionId, long userId) {
		this.permissionId = permissionId;
		this.userId = userId;
	}

	public UserPermissions(Permissions permissions, User user) {
		this.permissionId = permissions.getId();
		this.userId=user.getUserId();
		this.user = user;
	}

	//endregion

	//region Getters
	public long getUserPermissionsId() {
		return userPermissionsId;
	}

	public long getPermissionId() {
		return permissionId;
	}

	public List<Permissions> getPermissions() {
		return permissions;
	}

	public User getUser() {
		return user;
	}

	public long getUserId() {
		return userId;
	}

	//endregion

	//region Setters

	public void setPermissions(List<Permissions> permissions) {
		this.permissions = permissions;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserPermissionsId(long userPermissionsId) {
		this.userPermissionsId = userPermissionsId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}
	//endregion

	//region Methods

	@Override
	public String toString() {
		return "UserPermissions{" +
				"userId=" + userId +
				", user=" + user +
				", permissionId=" + permissionId +
				", permissions=" + permissions +
				'}';
	}

	//endregion
}
