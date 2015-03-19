# excel-cellformatter
Excelのセルの書式を解析してフォーマットするライブラリ。

# 使い方
## POIの場合

```java
Cell cell = /* セルの値の取得 */;

POICellFormatter cellFormatter = new CellFormatter();
String contents = cellForrmatter.format(cell);

```

## JExcelAPIの場合

```java
// シートの読み込み
final WorkbookSettings settings = new WorkbookSettings();
settings.setSuppressWarnings(true);
settings.setGCDisabled(true);

// 文字コードを「ISO8859_1」にしないと文字化けする
settings.setEncoding("ISO8859_1");
settings.setLocale(Locale.JAPANESE);

final Workbook workbook = Workbook.getWorkbook(in, settings);

Cell cell = /* セルの値の取得 */;

JXLCellFormatter cellFormatter = new JXLCellFormatter();
String contents = cellForrmatter.format(cell);

```

### JExcelAPIの場合で使用する注意事項
- 会計、日時の書式が正しくとれない場合がある。
- Excelファイルの読み込み時に、文字コードを「ISO8859_1」にする必要がある。
