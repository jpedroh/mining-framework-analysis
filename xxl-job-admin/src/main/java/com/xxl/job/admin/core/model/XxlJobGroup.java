package com.xxl.job.admin.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;


/**
 * Created by xuxueli on 16/9/30.
 */
@Entity
@Table(name = "xxl_job_group")
public class XxlJobGroup {
    // 使用基于雪花算法的主键生成策略
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityGenerator")
    @GenericGenerator(name = "IdentityGenerator", strategy = "com.xxl.job.admin.core.util.XxlJobGenerator")
    private Long id;

    @Column(name = "app_name", nullable = false, length = 64)
    private String appname;

    @Column(name = "title", nullable = false, length = 12)
    private String title;

    // 执行器地址类型：0=自动注册、1=手动录入
    @Column(name = "address_type", nullable = false, length = 12)
    private int addressType;

    // 执行器地址列表，多地址逗号分隔(手动录入)
    @Column(name = "address_list", length = 512)
    private String addressList;

    // registry list
    // 执行器地址列表(系统注册)
    @Transient
    private List<String> registryList;

    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }
}