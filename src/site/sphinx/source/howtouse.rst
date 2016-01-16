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
        <version>0.5</version>
    </dependency>


-----------------
Apaceh POIの場合
-----------------

`Apache POI <http://poi.apache.org/>`_ を利用する場合は、 *POICellFormatter* を利用します。

* 書式が「m/d/yy」の時など、実行環境の言語環境によって異なる場合は、ロケールを指定します。
  
  * ロケールを指定しない場合は、デフォルトのロケールになるため、Linux環境などのでは注意してください。

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
  
* 1904年始まり設定がされているExelファイルの設定かどうか、メソッド *JXLUtils.isDateStart1904(...)* で調べた値を渡します。
  
  * 通常は1899年12月31日（Excel表記上は 1900年1月0日）が基準です。
  
  * JXLUtils.isDateStart1904(...)メソッドには、Sheetオブジェクトを引数にとるメソッドも用意されています。

* 書式が「m/d/yy」の時など、実行環境の言語環境によって異なる場合は、ロケールを指定します。
  
  * ロケールを指定しない場合は、デフォルトのロケールになるため、Linux環境などのでは注意してください。

.. sourcecode:: java
    
    // シートの読み込み
    final WorkbookSettings settings = new WorkbookSettings();
    settings.setSuppressWarnings(true);
    settings.setGCDisabled(true);
    
    // 文字コードを「ISO8859_1」にしないと、一部の文字が文字化けします。
    settings.setEncoding("ISO8859_1");
    
    final Workbook workbook = Workbook.getWorkbook(in, settings);
    
    Cell cell = /* セルの値の取得 */;
    
    final JXLCellFormatter cellFormatter = new JXLCellFormatter();
    
    // JXLUtils.isDateStart1904(...)を利用して、1904年始まりのシートか調べる。
    String contents = cellForrmatter.formatAsString(cell, JXLUtils.isDateStart1904(workbook));
    
    // ロケールを指定してフォーマットする。
    contents = cellForrmatter.formatAsString(cell, Locale.JAPANESE, JXLUtils.isDateStart1904(workbook));



