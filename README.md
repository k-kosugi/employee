# EMPLOYEE
thorntailお勉強のために作成。  
ビルドツールをダウンロードしなくても済むのでGradle派だったが、thorntailはGradleだと色々不具合があり、Mavenで実装。  
Spring BootはJPAがHibernate標準だったり、REST API周りが独自実装であまり好きではないので、Java EEを適用可能なthorntailを実験中。

# 利用Fraction
認識しているのは以下だが、Mavenの依存関係自動解決で他にも色々適用されている様子。

|フラクション||
|---|---|
|jaxrs-jsonp||
|jpa-eclipselink||
|datasources|
|cdi||

# API 仕様(現時点)
http://<thorntail>:8080/api/employee/hello

EMPLOYEEテーブルにあるすべての従業員を引っ張りだしてくる。

# テーブル
コードファーストで実装しているので、thorntailを起動すると勝手にテーブルを作成します。
```java
package org.kenta.kosugi.employee.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "EMPLOYEE", indexes = {
        @Index(name = "IDX_FIRST_NAME", columnList = "FIRST_NAME"),
        @Index(name = "IDX_MIDDLE_NAME", columnList = "MIDDLE_NAME"),
        @Index(name = "IDX_LAST_NAME", columnList = "LAST_NAME")
})
@NamedQueries({
        @NamedQuery(name = "Employee.findAll", query = "select a from Employee a"),
        @NamedQuery(name = "Employee.findByName", query = "select a from Employee a where a.firstName like :name or a.lastName like :name")
})
public class Employee implements Serializable {

    private static final long serialVersionUID = -7763827188716065700L;

    @Id
    @Column(name = "ID", length = 8)
    public String id;

    @Column(name = "FIRST_NAME", length = 32)
    public String firstName;

    @Column(name = "MIDDLE_NAME", length = 32, nullable = true)
    public String middleName;

    @Column(name = "LAST_NAME", length = 32)
    public String lastName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Employee boss;

}

```
これから自動生成されるテーブルのスキーマは以下。
```sql
create table EMPLOYEE
(
	ID varchar(8) not null
		primary key,
	FIRST_NAME varchar(32) null,
	LAST_NAME varchar(32) null,
	MIDDLE_NAME varchar(32) null,
	BOSS_ID varchar(8) null,
	constraint FK_EMPLOYEE_BOSS_ID
		foreign key (BOSS_ID) references EMPLOYEE (ID)
);

create index IDX_FIRST_NAME
	on EMPLOYEE (FIRST_NAME);

create index IDX_LAST_NAME
	on EMPLOYEE (LAST_NAME);

create index IDX_MIDDLE_NAME
	on EMPLOYEE (MIDDLE_NAME);


```