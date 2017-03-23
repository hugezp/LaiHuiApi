package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.ApkUpdate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class ApkUpdatedMapper implements RowMapper<ApkUpdate> {

    public ApkUpdate mapRow(ResultSet resultSet, int i) throws SQLException {
        ApkUpdate apkUpdate=new ApkUpdate();

        apkUpdate.setId(resultSet.getInt("_id"));
        apkUpdate.setDownload_url(resultSet.getString("download_url"));
        apkUpdate.setVersionCode(resultSet.getInt("versionCode"));
        apkUpdate.setUpdateMessage(resultSet.getString("updateMessage"));
        apkUpdate.setCreate_time(resultSet.getString("create_time"));
        int soucre=resultSet.getInt("source");
        if(soucre==1){

            apkUpdate.setSoucre("iOS");
        }else {
            apkUpdate.setSoucre("android");
        }
        apkUpdate.setIs_must(resultSet.getInt("is_must"));
        return apkUpdate;
    }
}
