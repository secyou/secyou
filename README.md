# javafx_sapp

<br><br>
## ＜＜＜ミドルウェアバージョン＞＞＞
|ミドルウェア|バージョン|備考|
|:---|:---|:---|
|Java|OpenJdk 11||
|JavaFX|OpenJFX 14||
|MySQL|mysql-8.0.20||
|IDE|STS-4.6.1||

<br><br>
## ＜＜＜開発環境構築手順＞＞＞
#### 1、Java11のインストール
1-1 下記よりOpenJDK 11をダウンロードする<br>
　http://jdk.java.net/java-se-ri/11<br>
　※詳細は略<br>

#### 2、MySQLのインストール
2-1 下記よりMySQLをダウンロードする<br>
　バージョンは、8.0.20を利用<br>
　https://dev.mysql.com/downloads/mysql/<br>
　**※注意：ダウンロードものは、MySQL Community Server版のZIPファイルであり、Windows用インストーラ(.msi)ファイルではない**<br>
2-2 ダウンロードしたZIPファイルをロカールPCに解凍する<br>
2-3 起動用iniファイルを作成<br>
　任意のディレクトリにmyapp.iniファイルを作成して、下記の内容を記載する<br>
　basedir：上記2で解凍しファイルパスを記載<br>
　datadir：DBデータの格納先を記載(任意ディレクトリで問題なし)<br>
```
[mysqld]
basedir=C:\\mysql-8.0.20-winx64
datadir=C:\\mysql-8.0.20-winx64\\data
```
2-4 MySQL初期化を行う<br>
　コマンドプロンプトで下記のコマンドを実行する<br>
　初期化時にrootユーザのパスワードが自動生成されるので、コマンドプロンプトで表示されたパスワードをメモする<br>
```
>cd [MySQLの配置ディレクトリ]
>cd bin
>mysqld --defaults-file=[iniファイル配置先]\myapp.ini --initialize --console
```
　初期化設定が終わったら、次回からは下記コマンドでMySQLサービスを起動する<br>
```
>mysqld --defaults-file=[iniファイル配置先]\myapp.ini --console
```
2-5 rootユーザでMySQLに接続する<br>
　2-4で立ち上げたコマンドプロンプトはそのままにして、新にコマンドプロンプトを立ち上げる。<br>
　rootユーザでDBに接続する。<br>
```
>cd [MySQLの配置ディレクトリ]
>cd bin
>mysql -h localhost -u root -p
2-4でメモしたパスワードを入力する
```
2-6 rootユーザでパスワードを変更する<br>
```
mysql > ALTER USER 'root'@'localhost' IDENTIFIED BY '1qaz2wsx';
```
2-7 アプリ用DadaBase(名前:app)を作成する<br>
```
mysql > create database app;
mysql > show databases;
```
2-8 アプリ用DataBaseに切り替える<br>
```
mysql > use app
```
2-9 テーブル構築及びマスタデータを登録<br>
　テーブル作成のDLLとマスタデータ登録のDMLは、gitの「db」ディレクトリ配下を確認する<br>
　※詳細は略<br>

#### 3、ソースコードをダウンロード
3-1 githubからソースコードをローカルにCloneする<br>
　https://github.com/busicom-jp/javafx_sapp.git<br>
　※Gitのインストール・設定手順は、略<br>
3-2 IDEにインポートする<br>
　※詳細は略<br>
3-3 LombokをIDEにインストールする<br>
　Lombokを利用して、データモデルのGetter/Setterメソッドを生成するため、IDEにLombokをインストールする必要がある<br>
　バージョンは、「lombok-1.18.12.ja」を利用する<br>
　※詳細は略(困ったら、Google先生に)<br>

<br><br>
## ＜＜＜実行手順書＞＞＞
#### 1、MySQLを起動
```
>cd [MySQLの配置ディレクトリ]
>cd bin
>mysqld --defaults-file=[iniファイル配置先]\myapp.ini --console
```
#### 2、コマンドプロンプトでsrcディレクトリに移動
```
>cd [ソースコードの配置ディレクトリ]  
```
#### 3、実行する
```
>mvn clean compile exec:java
```
※mavenコマンド利用するため、Mavenを先にインストール必要
#### 4、アプリが立ち上がるをことを確認
