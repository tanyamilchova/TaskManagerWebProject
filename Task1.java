public class Task1 {
    public static void main(String[] args) {


/*    Задача 1:
Да се прочете масив и да се намери най-малкото число кратно на
3 от масива.

 */
        int[] arr = {10, 3, 5, 8, 6, -3, 7};
        int min = arr[0];
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            if (i == arr.length - 1) {
                System.out.print(arr[i] + " ");
            } else {
                System.out.print(arr[i] + ", ");
            }
        }
            System.out.print("]");
            for (int i = 1; i < arr.length - 1; i++) {
                if (arr[i] % 3 == 0) {
                    min = arr[i];
                    if (arr[i + 1] < min) {
                        min = arr[i + 1];
                    }
                }
            }
            System.out.println();
            System.out.println(min + " is the min divisible to 3");
        }
    }



