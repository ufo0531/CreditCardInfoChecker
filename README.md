# CreditCardInfoChecker

查詢信用卡BIN資訊
使用API：http://www.binlist.net/

---------------------------------------------------------

- 使用方式

java -jar CreditCardInfoChecker-with-deps.jar

會產生input和output資料夾，將輸入檔案放至input資料夾，檔案會輸出至output資料夾下，並且備份至input\backup下

範例格式：

    1,431940
    2,431940
    3,431940
    4,431940
    5,431940
    6,431940
    7,431940

輸出範例：

    1,VISA,IE,Ireland,BANK OF IRELAND,DEBIT,,
    2,VISA,IE,Ireland,BANK OF IRELAND,DEBIT,,
    3,VISA,IE,Ireland,BANK OF IRELAND,DEBIT,,
    4,VISA,IE,Ireland,BANK OF IRELAND,DEBIT,,
    5,VISA,IE,Ireland,BANK OF IRELAND,DEBIT,,
    6,VISA,IE,Ireland,BANK OF IRELAND,DEBIT,,
    7,VISA,IE,Ireland,BANK OF IRELAND,DEBIT,,
