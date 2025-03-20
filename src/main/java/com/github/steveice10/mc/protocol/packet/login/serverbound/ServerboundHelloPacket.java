package com.github.steveice10.mc.protocol.packet.login.serverbound;

import com.github.steveice10.mc.protocol.codec.MinecraftCodecHelper;
import com.github.steveice10.mc.protocol.codec.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.With;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
<<<<<<< /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/left.java
import java.util.UUID;
||||||| /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/base.java
=======
import java.time.Instant;
import java.util.UUID;
>>>>>>> /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/right.java

@Data
@With
@AllArgsConstructor
public class ServerboundHelloPacket implements MinecraftPacket {
    private final @NonNull String username;
<<<<<<< /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/left.java
    private final @Nullable Long expiresAt;
    private final @Nullable PublicKey publicKey;
    private final byte @Nullable[] keySignature;
    private final @Nullable UUID profileId;
||||||| /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/base.java
    private final @Nullable Long expiresAt;
    private final @Nullable PublicKey publicKey;
    private final @Nullable byte[] keySignature;
=======
    private final @Nullable ProfilePublicKeyData publicKey;
    private final @Nullable UUID profileId;
>>>>>>> /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/right.java

    public ServerboundHelloPacket(ByteBuf in, MinecraftCodecHelper helper) throws IOException {
        this.username = helper.readString(in);
        if (in.readBoolean()) {
            this.publicKey = new ProfilePublicKeyData(in, helper);
        } else {
            this.publicKey = null;
        }
        this.profileId = helper.readNullable(in, helper::readUUID);
    }

    @Override
    public void serialize(ByteBuf out, MinecraftCodecHelper helper) {
        helper.writeString(out, this.username);
        out.writeBoolean(this.publicKey != null);
        if (this.publicKey != null) {
            this.publicKey.serialize(out, helper);
        }
        helper.writeNullable(out, this.profileId, helper::writeUUID);
    }

    @Override
    public boolean isPriority() {
        return true;
    }

    // Likely temporary; will be moved to AuthLib when full public key support is developed
    public static class ProfilePublicKeyData {
        private final long expiresAt;
        private final PublicKey publicKey;
        private final byte[] keySignature;

        public ProfilePublicKeyData(ByteBuf in, MinecraftCodecHelper helper) throws IOException {
            this.expiresAt = in.readLong();
            byte[] publicKey = helper.readByteArray(in);
            this.keySignature = helper.readByteArray(in);

            try {
                this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
            } catch (GeneralSecurityException e) {
                throw new IOException("Could not decode public key.", e);
            }
        }
<<<<<<< /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/left.java
        if (in.readBoolean()) {
            this.profileId = helper.readUUID(in);
        } else {
            this.profileId = null;
        }
    }
||||||| /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/base.java
    }
=======
>>>>>>> /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/main/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacket.java/right.java

        public ProfilePublicKeyData(long expiresAt, PublicKey publicKey, byte[] keySignature) {
            this.expiresAt = expiresAt;
            this.publicKey = publicKey;
            this.keySignature = keySignature;
        }

        private void serialize(ByteBuf out, MinecraftCodecHelper helper) {
            out.writeLong(this.expiresAt);
            byte[] encoded = this.publicKey.getEncoded();
            helper.writeByteArray(out, encoded);
            helper.writeByteArray(out, this.keySignature);
        }
        out.writeBoolean(this.profileId != null);
        if (this.profileId != null) {
            helper.writeUUID(out, this.profileId);
        }

        @Contract("-> new")
        public Instant getExpiresAt() {
            return Instant.ofEpochMilli(this.expiresAt);
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public byte[] getKeySignature() {
            return keySignature;
        }
    }
}
