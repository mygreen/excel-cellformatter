# excel-cellformatter
Excelのセルの書式を解析してフォーマットするJavaのライブラリ。

# 前提条件
+ Java1.7-1.8
+ Apache POI v3.17
+ Slf4j v1.7+


# 設定

1. excel-cellformatterのライブラリの依存の追加
    ```xml
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>excel-cellformatter</artifactId>
        <version>0.10</version>
    </dependency>
    ```

2. ログライブラリの依存関係を追加します。例として、log4jのケースを以下に示します。
    ```xml
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.1</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.14</version>
    </dependency>
    ```

# ビルド方法

1. Java SE 7をインストール
2. Mavenのインストール
3. Sphinxのインストール（マニュアルのビルドに使用）
    a. Pythonのインストール
    b. SphinxとJanomeのインストール
    ```console
    # pip install sphinx
    # pip install janome
    ```
4. Mavenによるビルド
    a. jarの作成
    ```console
    # mven clean package
    ```
    b.サイト生成
    ```console
    # mven site -Dgpg.skip=true
    ```

# 簡単な使い方
## Apache POIの場合

Apache POIの場合、POICellFormatter クラスを使います。

```java
Cell cell = /* セルの値の取得 */;

POICellFormatter cellFormatter = new POICellFormatter();
String contents = cellForrmatter.formatAsString(cell);

// ロケールに依存する書式の場合
contents = cellForrmatter.formatAsString(cell, Locale.US);

// 文字色の条件が設定されている場合
CellFormatResult result = cellForrmatter.format(cell);
String text = result.getText(); // フォーマット結果の文字列
MSColor textColor = result.getTextColor(); // フォーマットした文字色

```

### 注意事項
- 単純にフォーマット結果を文字列として取得する場合、#formatAsString(...)メソッドを呼びます。
- 文字色など書式に条件が設定されている場合、詳細な情報を取得するには、#format(...)メソッドを呼びます。

## JExcelAPIの場合

JExcelAPIの場合、JXLCellFormatter クラスを使います。
基本的に、使い方は POICellFormatter と同じです。

```java
// シートの読み込み
final WorkbookSettings settings = new WorkbookSettings();
settings.setSuppressWarnings(true);
settings.setGCDisabled(true);

// 文字コードを「ISO8859_1」にしないと、会計の記号が文字化けする
settings.setEncoding("ISO8859_1");

final Workbook workbook = Workbook.getWorkbook(in, settings);

Cell cell = /* セルの値の取得 */;

JXLCellFormatter cellFormatter = new JXLCellFormatter();

String contents = cellForrmatter.formatAsString(cell);

// ロケールに依存する書式の場合
contents = cellForrmatter.formatAsString(cell, Locale.US);

// 1904年始まりのシートの場合
contents = cellForrmatter.formatAsString(cell, JXLUtils.isDateStart1904(sheet));

```

### JExcelAPIを使用する際の注意事項
- Excelファイルの読み込み時に、文字コードを「ISO8859_1」にする必要がある。指定しない場合は、書式のパターンが文字化けする。Windows-31jでも文字化けする。
- 1904年始まりのファイルの設定の場合、メソッド「JXLUtils.isDateStart1904(...)」で調べる。


## Javaのオブジェクトを直接フォーマットする場合

Javaのオブジェクトの場合、ObjectCellFormatter クラスを使います。
基本的に、使い方は POICellFormatter と同じです。

```java

Date date = Timestamp.valueOf("2012-02-01 12:10:00.000");

ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
String contents = cellForrmatter.formatAsString("yyyy\"年\"m\"月\"d\"日\";@", date);

// ロケールに依存する書式の場合
contents = cellForrmatter.formatAsString("yyyy\"年\"m\"月\"d\"日\";@", date, Locale.US);

// 文字色の条件が設定されている場合
CellFormatResult result = cellForrmatter.format("[Red]yyyy\"年\"m\"月\"d\"日\";@", date);
String text = result.getText(); // フォーマット結果の文字列
MSColor textColor = result.getTextColor(); // フォーマットした文字色

```


# ドキュメント
http://mygreen.github.io/excel-cellformatter/sphinx/howtouse.html
