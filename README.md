# 目的
thorntailお勉強のために作成。  
thorntailとはなんぞやという人は[ここ](https://www.publickey1.jp/blog/17/javamicroprofileeclipse.html)や[ここ](https://www.infoq.com/jp/news/2018/06/wildfly-thorntail)がおすすめ。

従業員管理サービスのマイクロサービスを実装する。  
throntailを導入してuberjarとしてKubernetes上で起動したい。

```
RedHatの人曰く、thorntailは終了するらしい。
続きはQUARKUSプロジェクトとWildFlyプロジェクトに別れるらしい。

QUARKUSはJavaソースコードをLihuxバイナリ(x86)に直接変換することでJava VMの巨大なFoot Printを抑える。
これにより、マイクロサービスとして適用しやすくなる。
何が言いたいかと言うとKubernetesに乗りやすくなるということ。
```

## ビルドツールやその他
ビルドツールをダウンロードしなくても済むのでGradle派だったが、thorntailはGradleだと色々不具合があり、Mavenで実装。  
Spring Boot(Spring BootはDell配下のPivotal開発て知ってました?)はJPAがHibernate標準だったり、REST API周りが独自実装であまり好きではないので、Java EEを適用可能なthorntailを実験中。
  
あと、SpringBoot使うとサーキットブレーカー等が使えるようですが、Istio使えば実装する必要なしです。  

# 使い方

## 1. MySQLの起動
データベースはMySQL on docker。
以下のコマンドを投入してdockerを起動。

1. Dockerfileが存在するディレクトリでdockerイメージをビルド。タグ名はnetapp/employeedb:v1だが他のものでも問題なし。
    ```
    $ docker build -t netapp/employeedb:v1 -f Dockerfile.mysql . 
    ```
2. イメージを元に起動。
    * 以下はビルド時に永続ボリュームを指定していないので注意が必要です。
    * もし、コンテナを削除してテーブルデータが消えたら問題がある人は -v employeedb_volume:/var/lib/mysqlをオプションで投入するなりして永続ボリュームを適用しましょう。
    ```
    $ docker run -d --name employeedb -p 3306:3306 netapp/employeedb:v1
    ```
## 2. Thorntailのビルドと起動
1. Mavenのpackageゴールを指定してwar/jarを作成
    ```
    $ mvn package
    ```
1. targetディレクトリに以下二つのjar/warが作成されることを確認。
    * target/employee.war
        * こちらがwarファイルのようです。試していませんが、Wildflyなどにデプロイすれば普通に動くのではないかと思います。
    * target/employee-thorntail.jar
        * こちらがuberjarと呼ばれるファイル。内部にthorntailを起動するためのBootStrapや起動に必要なjar群が固まって格納されています。
1. java コマンドを使用してjarファイルを起動。
    * ./employeディレクトリ上でjavaコマンドを投入する場合は -s以下不要。
    ```
    $ java -jar target/employee-thorntail.jar -s<project-defaults.yamlの相対パス>
    ```
## 3. MySQLへのデータの投入    
3. データの投入。 
    load.sqlをpersisntece.xmlに定義し、起動時にINSERT文を大量にロードする予定なので、本来は以下の処理は不要です。
    1. docker上のOSに入る。
        ```
        $ docker exec -it employeedb /bin/bash
        ```
    1. mysqlコマンドを叩いてMySQLにログイン
        ```
        $ mysql -u kenta -p
        kosugi
        ```
    1. employeedbにアタッチ
        ```
        mysql> use employeedb
        ```
    1. 適当なデータを入れてください。※必要なだけ
        ```
        mysql> insert into employee values('0347255', 'kenta', 'kosugi', null, null);
        ```

## 3. REST APIへアクセス
1. VSCodeのREST Clientなどを使用して、REST APIにアクセス。
    ```
    GET http://localhost:8080/api/employee/all
    ```
# 利用Fraction

|フラクション|役割|
|---|---|
|jaxrs-jsonp|JAX-RSとJSON-Pを使用する|
|jpa-eclipselink|JPAのEclipseLink実装を利用する|
|datasources|thorntail上からデータベースへのコネクションプールを張る|
|cdi|Dependency Injectionを利用する。|

認識しているのは上記だが、Mavenの依存関係自動解決で他にも色々適用されている様子。

# API 仕様(現時点)
1. すべてのEmployeeを検索
```
GET http://localhost:8080/api/employee/all
```
2. 特定のidで検索
```
GET http://localhost:8080/api/employee/{id}
```
3. Employeeを登録する
```
POST http://localhost:8080/api/employee/{id}?firstName={firstName}&middleName={middleName}&lastName={lastName}&hiredDate={yyyyDDmm}
```

thorntail起動マシン以外からアクセスするとクロスドメインの問題が発生すると思うので、[ここ](http://garapon.hatenablog.com/entry/2016/03/23/JAX-RS2.0でRESTサービスを作る際にヘッダーを指定する)の対策をしましょう
。--> クロスドメインの対応は実施済み

# テーブル
Code Firstで実装しているので、thorntailを起動すると勝手にテーブルを作成します。
```java
package org.kenta.kosugi.employee.model;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Employee class for employee.EMPLOYEE table.
 */
@Entity
@Table(name = "EMPLOYEE", indexes = {
        @Index(name = "IDX_FIRST_NAME", columnList = "FIRST_NAME"),
        @Index(name = "IDX_MIDDLE_NAME", columnList = "MIDDLE_NAME"),
        @Index(name = "IDX_LAST_NAME", columnList = "LAST_NAME")
})
@NamedQueries({
        @NamedQuery(name = "Employee.findAll", query = "select a from Employee a where a.leavedDate is not null"),
        @NamedQuery(name = "Employee.findByName", query = "select a from Employee a where (a.firstName like :name or a.lastName like :name) and a.leavedDate is not null")
})
public class Employee implements Serializable {

    private static final long serialVersionUID = -7763827188716065700L;

    /**
     * Primary key for this EMPLOYEE table.
     */
    @Id
    @Column(name = "ID", length = 8)
    private String id;

    /**
     * Get primary key for this employee.
     *
     * @return primary key.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the primary key for this employee.
     *
     * @param id primary key
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * First Name associated with this employee object.
     */
    @Column(name = "FIRST_NAME", length = 32)
    private String firstName;

    /**
     * Get the first name associated with this employee object.
     *
     * @return The first name associated with this employee object.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Set first name to employee object.
     *
     * @param firstName First name associated with this employee object.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "MIDDLE_NAME", length = 32, nullable = true)
    private String middleName;

    /**
     * Get Middle name associated with this employee.
     *
     * @return Middle name associated with this employee.
     */
    public String getMiddleName() {
        return this.middleName;
    }

    /**
     * Set Middle name to this employee.
     */
    public void setMiddleName() {
        this.middleName = middleName;
    }

    @Column(name = "LAST_NAME", length = 32)
    private String lastName;

    /**
     * Get Last name associated with this employee object.
     *
     * @return Last name associated with this employee object.
     */
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName() {
        this.lastName = lastName;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employee boss;

    /**
     * Return the boss object associated with this employee.
     *
     * @return The boss object associated with this employee.
     */
    public Employee getBoss() {
        return this.boss;
    }

    /**
     * Set boss object to this employee.
     *
     * @param boss The boss object to this employee.
     */
    public void setBoss(Employee boss) {
        this.boss = boss;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "HIRE_DATE", nullable = false)
    private Date hireDate;

    /**
     * Set the hired date associated with this employee.
     *
     * @param hireDate Hired Date.
     * @throws ParseException
     */
    public void setHiredDate(String hireDate) throws ParseException {
        this.hireDate = this.parse(hireDate);
    }

    /**
     * Get the hired Date associated with this employee.
     *
     * @return Hire Date associated with this employee.
     */
    public Date getHireDate() {
        return hireDate;
    }

    /**
     * The leaved Date.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "LEAVED_DATE")
    private Date leavedDate;

    /**
     * Set leaved date to employee object.
     *
     * @param leavedDate Leaved Date.
     * @throws ParseException Throw new ParseException when casting the String to Calendar object.
     */
    public void setLeavedDate(String leavedDate) throws ParseException {
        this.leavedDate = this.parse(leavedDate);
    }

    /**
     * Get the leaved Date associated with this employee.
     *
     * @return Leaved Date associated with this employee.
     */
    public Date getLeavedDate() {
        return this.leavedDate;
    }

    @Transient
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * Cast String object to Calendar object.
     *
     * @param date The date of String for example "20190901"
     * @return Return the Calendar object.
     * @throws ParseException Throw ParseException when SimpleDateFormat.parse(String) method fail.
     */
    @Transient
    private Date parse(String date) throws ParseException {

        if (date == null) {
            return null;
        }

        // parsing
        return this.simpleDateFormat.parse(date);

    }

    /**
     * Constructor
     */
    public Employee() {
    }

    /**
     * Constructor for Employee class without boss object.
     *
     * @param id         Company id.
     * @param firstName  First Name.
     * @param middleName Middle Name.
     * @param lastName   Last Name.
     * @param hireDate   Hired Date.
     */
    public Employee(String id, String firstName, String middleName, String lastName, String hireDate) throws ParseException {

        this.id = id;
        this.firstName = firstName;

        if (!"".equals(middleName)) {
            // set the null when middleName is empty.
            this.middleName = middleName;
        }

        this.lastName = lastName;
        this.setHiredDate(hireDate);

    }

}

```
これから自動生成されるテーブルのスキーマは以下。
```sql
create table EMPLOYEE
(
	ID varchar(8) not null
		primary key,
	FIRST_NAME varchar(32) null,
	HIRE_DATE date not null,
	LAST_NAME varchar(32) null,
	LEAVED_DATE date null,
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

# 確認している問題
* MySQLのコネクタを8.xもしくは6.xの新し目のものにするとthorntailが起動しない。
* MavenではなくGradleを利用すると同じコードでも起動しない。packageゴールで色々問題が発生している？
* MySQLを依存関係に入れてthrontailを起動すると勝手にDataSourceとして認識する様子。
  * RESOURCE_LOCALは認識しない？
  
# 今後
thorntail + MySQLを一つのPodとしてKubernetesにデプロイして運用できるようにする。
