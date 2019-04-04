public class AES {

    private byte [] key;
    private byte [][] state;
    private byte [] M = null;
    private byte [] C = null;

    public AES() {
        this.key = null;
        this.M = new byte[16];
        this.C = new byte[16];
        this.state = new byte[4][4];
    }

    public AES(byte[] key) {
        this.key = key;
        this.M = new byte[16];
        this.C = new byte[16];
        this.state = new byte[4][4];
    }

    private void createState(byte [] input) {
        int plainTextIt = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                state[j][i] = input[plainTextIt];
                ++plainTextIt;
            }
        }
    }

    private void createMatrixKey(byte [][] matrixKey) {
        int index = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                matrixKey[j][i] = key[index];
                ++index;
            }
        }
    }

    private void createMatrixCipher(byte [][] matrixCipher) {
        int index = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                matrixCipher[j][i] = C[index];
                ++index;
            }
        }
    }

    private void shiftRows() {
        byte[] shiftBuffer = new byte[3];
        for (int i = 1; i < 4; ++i) {
            int toCopy = i; // Avoid recreating shiftBuffer
            System.arraycopy(state[i], 0, shiftBuffer, 0, i);
            System.arraycopy(state[i], i, state[i], 0, state[i].length - i);
            System.arraycopy(shiftBuffer, 0, state[i], state[i].length - i, toCopy);
        }
    }

    private void addRoundKey() {
        // XOR state with encryption key
        byte [][] matrixKey = new byte[4][4];
        createMatrixKey(matrixKey);
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                state[i][j] = (byte) (state[i][j] ^ matrixKey[i][j]);
            }
        }
    }


    private void createByteArray(byte [] output) {
        int plainTextIt = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                output[plainTextIt] = state[j][i];
                ++plainTextIt;
            }
        }
    }

    public byte [] breakAES(byte [] MTag, byte [] C){
        this.C = C;
        byte [][] matrixCipher = new byte[4][4];
        createMatrixCipher(matrixCipher);
        byte [] keyFound = new byte [16];
        createState(MTag);
        shiftRows();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                state[i][j] = (byte) (matrixCipher[i][j] ^ state[i][j]);
            }
        }
        createByteArray(keyFound);
        return keyFound;
    }

    public byte [] encrypt(byte [] M) {
        this.M = M;
        createState(M);
        shiftRows();
        addRoundKey();
        createByteArray(C);
        return C;
    }


    public byte [] decrypt(byte [] C) {
        this.C = C;
        createState(C);
        addRoundKey();
        for (int i = 0; i < 3 ; i++) {
            shiftRows();
        }
        createByteArray(M);
        return M;
    }

}
