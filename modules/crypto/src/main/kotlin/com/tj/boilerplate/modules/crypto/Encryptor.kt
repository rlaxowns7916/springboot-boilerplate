package com.tj.boilerplate.modules.crypto

/**
 * 대칭키 암복호 경계. 저장 전 [encrypt], 조회 후 [decrypt]한다.
 * 인터페이스로 둬 소비 측 테스트가 실 키 없이 fake(항등) 구현으로 대체할 수 있게 한다.
 */
interface Encryptor {
    fun encrypt(plaintext: String): String

    fun decrypt(ciphertext: String): String
}
