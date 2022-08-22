package com.poc.kubeappswrapper.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "app_tbl")
public class AppDetails {

	@Id
	private String appName;
	
	private String contextCluster;
	
	private String contextNamespace;
	
	private String packageIdentifier;
	
	private String pluginName;
	
	private String pluginVersion;
	
	private String packageVersion;
	
	@Lob 
	private String expectedInputData;
	
	@Lob 
	private String outputData;
	
	@Lob 
	private String requiredYamlConfiguration;
	
	@Lob 
	private String yamlValueFieldType;
	
}
