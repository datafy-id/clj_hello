# Hello CLJ

My latest clj stuff for random things.

## The things

**PHP mcrypt_rijndael_256 in Clojure**

The actually working php rijndael 256 port to clojure for now is in `me.php-mcrypt** namespace.

**CozoZB C API FFM with Clojure**

Using [`coffi`](https://github.com/IGJoshua/coffi) for [CozoDB's C Lib](https://github.com/cozodb/cozo/tree/main/cozo-lib-c)

As far as i can dig it for now, the minimum implementation is in `me.cozo-clj-ffi**.

**Datelevin 0.9.22**

Previous version that i use `0.9.13` circa November 2024. Latest version as of now (Jun 2025) require `libomp` to be installed in the host. Below are note to install libomp for some os.

- MacOS

1. Download `libomp.dylib` from `mac.r-project.org/openmp`.
2. Put it in expected place.

```bash
sudo mkdir /opt/homebrew/opt/llvm/lib
curl -O https://mac.r-project.org/openmp/openmp-17.0.6-darwin20-Release.tar.gz
tar zxvf openmp-17.0.6-darwin20-Release.tar.gz
sudo cp -rp usr/local/lib/libomp.dylib /opt/homebrew/opt/llvm/lib
```

- Fedora

```bash
sudo dnf install libomp
```

- NixOS

To my limited knowledge, the best workaround i can find and apply is:

1. Find correct `libgomp` in nix store, e.g. `cd /nix/store && fd libgomp.so`
2. Put the path into `LD_LIBRARY_PATH`.

```
export LD_LIBRARY_PATH=/nix/store/7n3q3rgy5382di7ccrh3r6gk2xp51dh7-gcc-14.2.1.20250322-lib/lib
```
