import java.io.*;

public class Scanner {

    private boolean isEof = false;
    private char ch = ' '; 
    private BufferedReader input;
    private String line = "";
    private int lineno = 0;
    private int col = 1;
    private final String letters = "abcdefghijklmnopqrstuvwxyz"
        + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String digits = "0123456789";
    private final char eolnCh = '\n';
    private final char eofCh = '\004';
    

    public Scanner (String fileName) { // source filename
    	System.out.println("Begin scanning... programs/" + fileName + "\n");
        try {
            input = new BufferedReader (new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.exit(1);
        }
    }

    private char nextChar() { //다음 문자 반환
        if (ch == eofCh)
            error("Attempt to read past end of file");
        col++;//몇 번째 문자를 반환할 지 결정
        if (col >= line.length()) {
            try {
                line = input.readLine( );//한 줄 읽어옴
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            } // try
            if (line == null) //line이 비어있으면
                line = "" + eofCh;//파일의 끝
            else {
                // System.out.println(lineno + ":\t" + line);
                lineno++;
                line += eolnCh;
            } // if line
            col = 0;
        } // if col
        return line.charAt(col);//col번째 문자 반환
    }
            

    public Token next( ) { //nextChar로 받아온 문자를 토큰으로 인식해서 반환
        do {
            try {
                if (isLetter(ch) || ch == '_') { // ident or keyword
                    String spelling = concat(letters + digits + '_');
                    return Token.keyword(spelling);
                } else if (isDigit(ch) || ch == '.') { // int or double literal
                    String number = "";
                    boolean isDouble = false;//실수인지 인식하는 boolean 변수

                    if (ch == '.') {//맨처음. 이 나오는 경우(.123)
                        isDouble = true;//실수
                        number += ch;
                        ch = nextChar();
                    }

                    number += concat(digits);//숫자값을 number 스트림에 붙여줌

                    if (ch == '.') {//숫자 다음 .이 나오는 경우 (123. , 1.23)
                        isDouble = true;//실수
                        number += ch;
                        ch = nextChar();
                        if (isDigit(ch)) {
                            number += concat(digits);
                        }
                    }

                    if (isDouble) return Token.mkDoubleLiteral(number);//isDouble이면 doubleliteral 반환
                    else return Token.mkIntLiteral(number);//아니면 intliteral반환

                } else switch (ch) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case eolnCh:
                        ch = nextChar();
                        break;

                    case '/':  // divide or divAssign or comment
                        ch = nextChar();
                        if (ch == '=') { // divAssign
                            ch = nextChar();
                            return Token.divAssignTok;
                        }

                        // divide
                        if (ch != '*' && ch != '/') return Token.divideTok;

                        // multi line comment
                        if (ch == '*') {
                            do {
                                while (ch != '*') ch = nextChar();
                                ch = nextChar();
                            } while (ch != '/');
                            ch = nextChar();
                        }
                        // single line comment
                        else if (ch == '/') {
                            do {
                                ch = nextChar();
                            } while (ch != eolnCh);
                            ch = nextChar();
                        }

                        break;

                    case '\'':  // 문자 인식
                        char ch1 = nextChar();// ' 이후 나오는 첫 번째 문자는 문자임
                        nextChar(); // get '
                        ch = nextChar();
                        return Token.mkCharLiteral("" + ch1);//그 문자에 대해 DoubleLiteral 반환

                    case eofCh:
                        return Token.eofTok;

                    case '\"'://문자열 인식
                        ch = nextChar();// " 이후 나오는 문자는 문자열의 일부분
                        String str = "";
                        while (ch != '\"') { // " 이 나올 때 까지 계속 문자를 받아 하나로 합쳐줌
                            str += ch;
                            ch = nextChar();
                        }
                        ch = nextChar();
                        return Token.mkStringLiteral(str);//그렇게 만들어진 문자열을 StringLiteral로 반환

                    case '+':
                        ch = nextChar();
                        if (ch == '=') { // addAssign
                            ch = nextChar();
                            return Token.addAssignTok;
                        } else if (ch == '+') { // increment
                            ch = nextChar();
                            return Token.incrementTok;
                        }
                        return Token.plusTok;

                    case '-':
                        ch = nextChar();
                        if (ch == '=') { // subAssign
                            ch = nextChar();
                            return Token.subAssignTok;
                        } else if (ch == '-') { // decrement
                            ch = nextChar();
                            return Token.decrementTok;
                        }
                        return Token.minusTok;

                    case '*':
                        ch = nextChar();
                        if (ch == '=') { // multAssign
                            ch = nextChar();
                            return Token.multAssignTok;
                        }
                        return Token.multiplyTok;

                    case '%':
                        ch = nextChar();
                        if (ch == '=') { // remAssign
                            ch = nextChar();
                            return Token.remAssignTok;
                        }
                        return Token.reminderTok;

                    case '(':
                        ch = nextChar();
                        return Token.leftParenTok;

                    case ')':
                        ch = nextChar();
                        return Token.rightParenTok;

                    case '{':
                        ch = nextChar();
                        return Token.leftBraceTok;

                    case '}':
                        ch = nextChar();
                        return Token.rightBraceTok;

                    case ';':
                        ch = nextChar();
                        return Token.semicolonTok;
                    case ':'://switch, case, default문이 추가되어 콜론도 추가
                        ch = nextChar();
                        return Token.colonTok;
                    case ',':
                        ch = nextChar();
                        return Token.commaTok;

                    case '&':
                        check('&');
                        return Token.andTok;
                    case '|':
                        check('|');
                        return Token.orTok;

                    case '=':
                        return chkOpt('=', Token.assignTok,
                                Token.eqeqTok);
                    case '<':
                        return chkOpt('=', Token.ltTok,
                                Token.lteqTok);
                    case '>':
                        return chkOpt('=', Token.gtTok,
                                Token.gteqTok);
                    case '!':
                        return chkOpt('=', Token.notTok,
                                Token.noteqTok);

                    default:
                        error("Illegal character " + ch);
                        ch = nextChar(); // 에러 이후에도 계속 실행

                } // switch
            }catch(Exception e){
                System.err.println(e.getMessage());
                ch = nextChar(); // 에러 이후에도 계속 실행
            }
        } while (true);

    } // next


    private boolean isLetter(char c) {
        return (c>='a' && c<='z' || c>='A' && c<='Z');
    }
  
    private boolean isDigit(char c) {
        return (c>='0' && c<='9');
    }

    private void check(char c) {
        ch = nextChar();
        if (ch != c) 
            error("Illegal character, expecting " + c);
        ch = nextChar();
    }

    private Token chkOpt(char c, Token one, Token two) {
        ch = nextChar();
        if (ch != c)
            return one;
        ch = nextChar();
        return two;
    }

    private String concat(String set) {
        String r = "";
        do {
            r += ch;
            ch = nextChar();
        } while (set.indexOf(ch) >= 0);
        return r;
    }

    public void error (String msg) {
        System.err.print(line);
        System.err.println("Error: column " + col + " " + msg);
//        System.exit(1); // 에러 이후에도 계속 실행
    }

    static public void main ( String[] argv ) {
        Scanner lexer = new Scanner(argv[0]);
        Token tok = lexer.next();
        while (tok != Token.eofTok) {
            System.out.println(tok.toString());
            tok = lexer.next();
        } 
    } // main
}