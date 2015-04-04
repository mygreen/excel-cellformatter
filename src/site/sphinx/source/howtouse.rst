======================================
基本的な使い方
======================================

-----------------
ダウンロード
-----------------

Mavenのセントラルリポジトリに登録してあるため、依存関係として下記を追加します。

.. sourcecode:: xml
    
    <dependencies>
        
        <dependency>
            <groupId>com.github.mygreen</groupId>
            <artifactId>excel-cellformatter</artifactId>
            <version>0.1</version>
        </dependency>
        
    </dependencies>


-----------------
Apaceh POIの場合
-----------------

`Apache POI <http://poi.apache.org/>`_ を利用する場合は、 *POICellFormatter* を利用します。

* 書式が「m/d/yy」の時など、実行環境の言語環境によって異なる場合は、ロケールを指定します。
  
  * ロケールを指定しない場合は、デフォルトのロケールになるため、Linux環境などのでは注意してください。

.. sourcecode:: java
    
    Cell cell = /* セルの値の取得 */;
    
    final POICellFormatter cellFormatter = new POICellFormatter();
    
    String contents = cellForrmatter.format(cell);
    
    // ロケールを指定してフォーマットする。
    contents = cellForrmatter.format(cell, Locale.JAPANESE);


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
    String contents = cellForrmatter.format(cell, JXLUtils.isDateStart1904(workbook));
    
    // ロケールを指定してフォーマットする。
    contents = cellForrmatter.format(cell, Locale.JAPANESE, JXLUtils.isDateStart1904(workbook));



