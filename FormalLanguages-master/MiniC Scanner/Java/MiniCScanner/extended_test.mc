int main() {

    char c = 'A';
    double d = 3.14;
    double e = .123;
    double f = 123.;
    string g = "abcdefg";

    for (int i = 0; i < 5; i++) {
        i = i + 1;
    }

    int j = 0;
    do {
        j++;
    } while (j < 3);

    goto skip;
    printf("This will be skipped.\n");
    skip:
    printf("This will be printed.\n");

    int x = 2;
    switch (x) {
        case 1:
            printf("Case 1\n");
            break;
        case 2:
            printf("Case 2\n");
            break;
        default:
            printf("Default case\n");
    }

    return 0;
}