package com.seatus.Interfaces;

import com.seatus.Models.FireStoreUserDocument;
import com.seatus.Models.UserItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rah on 22-Nov-17.
 */

public interface InvitedUsersIterface {
    void allInvitedMembers(List<UserItem> invitedMembers);
}