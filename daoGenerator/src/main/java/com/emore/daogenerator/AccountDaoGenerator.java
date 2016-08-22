package com.emore.daogenerator;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class AccountDaoGenerator {

    private static final int VERSION = 1;

    public static void main(String[] args) throws Exception {
        Schema sch = new Schema(VERSION, "com.caij.emore.account");
        sch.setDefaultJavaPackageDao("com.caij.emore.account");
        sch.enableKeepSectionsByDefault();
        createAccount(sch);
        createToken(sch);
        new de.greenrobot.daogenerator.DaoGenerator().generateAll(sch, "./app/src/main/java");
    }

    private static void createToken(Schema sch) {
        Entity token = sch.addEntity("Token");
        token.addStringProperty("key").primaryKey();
        token.addStringProperty("uid");
        token.addStringProperty("access_token");
        token.addLongProperty("expires_in");
        token.addLongProperty("create_at");
    }

    private static void createAccount(Schema sch) {
        Entity account = sch.addEntity("Account");
        account.addLongProperty("uid").primaryKey();
        account.addStringProperty("username");
        account.addStringProperty("pwd");
        account.addIntProperty("status");
    }

}
