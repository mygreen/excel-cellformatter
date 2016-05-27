======================================
基本的な使い方
======================================

-----------------
ダウンロード
-----------------

Mavenのセントラルリポジトリに登録してあるため、依存関係として下記を追加します。

.. sourcecode:: xml
    
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>excel-cellformatter</artifactId>
        <version>0.8.2</version>
    </dependency>


-----------------
Apaceh POIの場合
-----------------

`Apache POI <http://poi.apache.org/>`_ を利用する場合は、 *POICellFormatter* を利用します。

* 書式が「m/d/yy」の時など、実行環境の言語設定によって異なる場合は、ロケールを指定します。
  
  * ロケールを指定しない場合は、デフォルトのロケールになるため、Linux環境などのでは注意してください。

* Cellのインスタンスがnullの場合、空（Blank）セルとして扱います。

  * POIの場合、データの入力がない領域のセルは、nullとなるためです。
  * これはJExcelAPIの仕様に合わせるためです。

* 結合されたセルの場合、結合領域を走査し、非空セルがそのセルの値を評価します。

  * POIの場合、結合されたセルの領域は、基本的に左上のセルに値が設定され、それ以外のセルは空セルとなるためです。
  
  * ただし、古いバージョンのExcelだったり、プログラムから出力したExcelファイルの場合、
    必ずしも左上のセルに値が入っているとは限らないため、結合した全ての領域を走査します。
  
  * これはJExcelAPIの仕様に合わせるためです。
  
* 数式や関数が設定されたセルの場合、それらを評価した結果を返します。

  * POIが対応していない数式や関数の場合、Excel上では正しく表示されていても、エラーセルの扱いとなります。
  * 使用するPOIのバージョンによって対応する関数も異なります。



.. sourcecode:: java
    
    Cell cell = /* セルの値の取得 */;
    
    final POICellFormatter cellFormatter = new POICellFormatter();
    
    String contents = cellForrmatter.formatAsString(cell);
    
    // ロケールを指定してフォーマットする。
    contents = cellForrmatter.formatAsString(cell, Locale.JAPANESE);



* 書式に色の条件式など指定されている場合、詳細の結果を取得することもできます。

.. sourcecode:: java
    
    final POICellFormatter cellFormatter = new POICellFormatter();
    
    // フォーマット結果の詳細を取得する
    CellFormatResult result = cellForrmatter.format(cell);
    
    // フォーマットした文字列の取得
    String text = result.getText();
    
    // 文字色が設定されている場合、その色の取得。
    // 色が設定されていない場合は、nullを返す。
    MSColor color = result.getTextColor();
    
    // フォーマット対象の値を取得します。
    // どのタイプの書式として処理されたかによって、値の取得方法がことなります。
    if(result.isDate()) {
        Date dateValue = result.getValueAsDate();
        
    } else if(result.isNumber()) {
        double numberValue = result.getValueAsDouble();
    
    } else if(result.isText()) {
        String stringValue = rsult.getValueAsString();
        
    } else {
        // エラー用セルやブランクセルの場合
        
    }


-----------------
JExcelAPIの場合
-----------------

`JExcelAPI <http://jexcelapi.sourceforge.net/>`_ を利用する場合は、 *JXLCellFormatter* を利用します。

* 日本語を含むExcelファイルの場合、文字コードを *ISO8859_1* を指定します。
  
  * 指定しない場合は、会計の書式中の円記号 *￥* が文字化けします。
  
  * *Windows-31j* と指定しても文字化けするため、注意してください。
  
* 1904年始まりの設定がされているExelファイルの設定かどうか、メソッド *JXLUtils.isDateStart1904(...)* で調べた値を渡します。
  
  * 通常は1899年12月31日（Excel表記上は 1900年1月0日）が基準です。
  
  * JXLUtils.isDateStart1904(...)メソッドには、Sheetオブジェクトを引数にとるメソッドも用意されています。

* 書式が「m/d/yy」の時など、実行環境の言語設定によって切り替わる場合は、ロケールを指定します。
  
  * ロケールを指定しない場合は、デフォルトのロケールになるため、Linux環境などでは注意してください。

.. sourcecode:: java
    
    // シートの読み込み
    final WorkbookSettings settings = new WorkbookSettings();
    settings.setSuppressWarnings(true);
    settings.setGCDisabled(true);
    
    // 文字コードを「ISO8859_1」にしないと、一部の文字が文字化けします。
    settings.setEncoding("ISO8859_1");
    
    final Workbook workbook = Workbook.getWorkbook(in, settings);
    
    // 1904年始まりのシートか調べる。
    boolean startDate1904 = JXLUtils.isDateStart1904(workbook);
    
    Cell cell = /* セルの値の取得 */;
    
    final JXLCellFormatter cellFormatter = new JXLCellFormatter();
    
    // セルの値を文字列として取得
    String contents = cellForrmatter.formatAsString(cell, startDate1904);
    
    // ロケールを指定してフォーマットする。
    contents = cellForrmatter.formatAsString(cell, Locale.JAPANESE, startDate1904);


.. _howObjectCellFormatter:

----------------------------------
Javaオブジェクトの場合
----------------------------------

Javaのオブジェクトを直接フォーマットすることもできます。

直接フォーマットをする場合、``ObjectCellFormatter`` (**ver0.6から利用可能**)を使用します。

* フォーマットするには、``ObjectCellFormatter#formatAsString(<書式>, <値>)`` を利用します。

* フォーマット可能なクラスは、Excelの型に対応する次のクラスになります。

  * 文字列値： ``String``
  * ブール値： ``boolean/Boolean``
  * 数値
    
    * プリミティブ型 ``byte/short/int/long/float/double`` 
    * プリミティブ型のラッパークラス ``Byte/Short/Integer/Long/Float/Double`` 。
    * ``java.math.Number`` のサブクラス ``AtomicInteger/AtomicLong/BigDecimal/BigInteger`` 。
    
  * 日時： ``java.util.Date`` とそのサブクラス ``java.sql.Date/java.sql.Time/java.sql.Timestamp`` 。

.. sourcecode:: java

    // 各型に対応したインタフェースを利用します。
    ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
    String text = cellFormatter.formatAsString("yyyy\"年\"m\"月\"d\"日\";@", Timestamp.valueOf("2012-02-01 12:10:00.000"));


* 細かく設定を行いたい場合は、仮想的なセル ``ObjectCell`` のサブクラスのインスタンスを引数に渡します。

  * 文字列型を表すセル： ``TextCell``
  * ブール型を表すセル： ``BooleanCell``
  * 数値型を表すセル： ``NumberCell``
  * 日時型を表すセル： ``DateCell`` 。1904年始まりなどの設定ができます。

.. sourcecode:: java

    //仮想的なセルのクラス「ObejctCell」の、型に合った具象クラスを利用します。
    ObejctCell cell = new DateCell(Timestamp.valueOf("2012-02-01 12:10:00.000"), "yyyy\"年\"m\"月\"d\"日\";@", false)
    CellFormatResult result = cellFormatter.format(cell);
    
    // フォーマットした文字列の取得
    String text = result.getText();
    
    // 文字色が設定されている場合、その色の取得。
    MSColor color = result.getTextColor();


