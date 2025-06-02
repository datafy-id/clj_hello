(ns me.php-mcrypt
  (:import
   [java.nio.charset StandardCharsets]
   [org.bouncycastle.crypto.engines RijndaelEngine]
   [org.bouncycastle.crypto.paddings PaddedBufferedBlockCipher ZeroBytePadding]
   [org.bouncycastle.crypto.params KeyParameter]
   [org.bouncycastle.util.encoders Base64 Hex]))

(defn php-safe-b64-encode [^bytes data]
  (-> (Base64/encode data)
      (String. StandardCharsets/US_ASCII)
      (.replace "+" "-")
      (.replace "/" "_")
      (.replaceAll "=" ""))) ; optional in PHP, so often removed

(defn php-safe-b64-decode [^String s]
  (let [padded (str s (apply str (repeat (mod (- 4 (mod (count s) 4)) 4) "=")))
        fixed (-> padded
                  (.replace "-" "+")
                  (.replace "_" "/"))]
    (Base64/decode (.getBytes fixed StandardCharsets/US_ASCII))))

(defn prepare-key [^String key-str]
  (let [key-bytes (.getBytes key-str StandardCharsets/US_ASCII)
        keysize (cond
                  (<= (count key-bytes) 16) 16
                  (<= (count key-bytes) 24) 24
                  :else 32)
        padded (byte-array keysize)]
    (System/arraycopy key-bytes 0 padded 0 (min keysize (count key-bytes)))
    padded))

(defn padded-crypt [^bytes input ^bytes key encrypt?]
  (let [cipher (RijndaelEngine. 256)
        padding (ZeroBytePadding.)
        cipher (PaddedBufferedBlockCipher. cipher padding)
        _ (.init cipher encrypt? (KeyParameter. key))
        out-buf (byte-array (.getOutputSize cipher (alength input)))
        processed-len (.processBytes cipher input 0 (alength input) out-buf 0)
        final-len (.doFinal cipher out-buf processed-len)]
    (java.util.Arrays/copyOf out-buf (+ processed-len final-len))))

(defn encrypt [plaintext-str key-str]
  (let [key (prepare-key key-str)
        input-bytes (.getBytes plaintext-str StandardCharsets/UTF_8)
        encrypted (padded-crypt input-bytes key true)]
    {:hex (String. (Hex/encode encrypted) StandardCharsets/US_ASCII)
     :safe-b64 (php-safe-b64-encode encrypted)}))

(defn decrypt [safe-b64-ciphertext key-str]
  (let [key (prepare-key key-str)
        cipher-bytes (php-safe-b64-decode safe-b64-ciphertext)
        decrypted-bytes (padded-crypt cipher-bytes key false)
        result (String. decrypted-bytes StandardCharsets/UTF_8)]
    (.replaceAll result "\u0000+$" ""))) ; strip null padding


;; Example usage
(comment
  (encrypt "{\"name\":\"ridho\",\"city\":\"cilegon\"}" "SECRET*333")
  (decrypt "u4a1V2UJn_Z7gU8Jfu7Dl74GwULGiAU4nrMykQxW7PCGRXr7Npps7AUwZoRiq3OlxqBnseyauKHP-z7Xow9HUg" "SECRET*333")
)
