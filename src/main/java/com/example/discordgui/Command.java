package com.example.discordgui;

public enum Command{
    GETPROFILEPICTURE("getProfilePicture"),
    GETPHONE("getPhone"),
    GETPHONEAGAIN("getPhoneAgain"),
    GETCHANNELNAME("getChannelName"),
    GETCHANNELNAMEAGAIN("getChannelNameAgain"),
    GETROLENAME("getRoleName"),
    GETROLENAMEAGAIN("getRoleNameAgain"),
    GETSERVERNAMEAGAIN("getServerNameAgain"),
    GETSERVERNAME("getServerName"),
    GETWELLCOME("GetWellCome"),
    ENTERCHATMODE("enterChatMode"),
    EXITCHATMODE("#exitChatMode"),
    CREATEFRIEND("createFriend"),
    PRINT("print"),
    GETUSERNAME("getUserName"),
    GETUSERNAMEAGAIN("getUserNameAgain"),
    GETPASSWORD("getPassword"),
    GETPASSWORDAGAIN("getPasswordAgain"),
    GETEMAIL("getEmail"),
    GETEMAILAGAIN("getEmailAgain"),
    SHOWMENU("showMenu"),
    RESETMENU("resetMenu"),
    PRINTWELLCOME("PrintWellCome"),
    GETTABLE("getTable"),
    SHOWFRIENDSCHART_ONLINE("ShowFriendsChart_Online"),
    SHOWFRIENDSCHART_ALL("ShowFriendsChart_All"),
    SHOWFRIENDSCHART_PENDING("ShowFriendsChart_Pending"),
    SHOWFRIENDSCHART_BLOCKED("ShowFriendsChart_Blocked"),
    PROFILEPAGE("ProfilePage"),
    SIGNUP("SignUp"),
    LOGIN("login"),
    EXIT("exit")
    ;

    private String str;

    Command(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public static Command valueOfLabel(String str) {
        for (Command e : values()) {
            if (e.getStr().equals(str)) {
                return e;
            }
        }
        return null;
    }
}