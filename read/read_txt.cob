       IDENTIFICATION DIVISION.
       PROGRAM-ID. BankCalc.

       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT InFile ASSIGN TO "input_01_0005.txt"
               ORGANIZATION IS LINE SEQUENTIAL.

       DATA DIVISION.
       FILE SECTION.
       FD InFile.
       01 InRecord               PIC X(100).

       WORKING-STORAGE SECTION.
       01 WS-EOF                 PIC X VALUE "N".
       01 WS-Field               PIC X(30).
       01 WS-Value               PIC X(70).

       01 StatusCode            PIC 9.
       01 PreviousBalance       PIC 9(9) VALUE 0.
       01 Amount                PIC 9(9) VALUE 0.
       01 NewBalance            PIC 9(9) VALUE 0.

       PROCEDURE DIVISION.
       BEGIN.
           OPEN INPUT InFile
           PERFORM UNTIL WS-EOF = "Y"
               READ InFile INTO InRecord
                   AT END MOVE "Y" TO WS-EOF
                   NOT AT END
                       UNSTRING InRecord
                           DELIMITED BY "="
                           INTO WS-Field, WS-Value
                       EVALUATE WS-Field
                           WHEN "StatusCode"
                               MOVE FUNCTION NUMVAL(WS-Value) TO 
                               StatusCode
                           WHEN "PreviousBalance"
                               MOVE FUNCTION NUMVAL(WS-Value) TO 
                               PreviousBalance
                           WHEN "Amount"
                               MOVE FUNCTION NUMVAL(WS-Value) TO Amount
                       END-EVALUATE
           END-PERFORM
           CLOSE InFile

           *> 执行加/减法
           EVALUATE StatusCode
               WHEN 1
                   ADD Amount TO PreviousBalance GIVING 
                   NewBalance
                   DISPLAY "执行存款操作"
               WHEN 2
                   SUBTRACT Amount FROM PreviousBalance GIVING 
                   NewBalance
                   DISPLAY "执行取款操作"
               WHEN OTHER
                   DISPLAY "未知操作类型"
           END-EVALUATE

           DISPLAY "新余额: " NewBalance

           STOP RUN.
