package dev.andante.mccic.hud.client.render;

import dev.andante.mccic.hud.MCCICHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Environment(EnvType.CLIENT)
public class MCCICLogoTexture extends ResourceTexture {
    public static final int MCCIC_BACKGROUND_COLOR = ColorHelper.Argb.getArgb(255, 199, 94, 8);
    public static final Identifier MCCIC_LOGO = new Identifier(MCCICHud.MOD_ID, "textures/gui/title/mojangstudios.png");

    public MCCICLogoTexture() {
        super(MCCIC_LOGO);
    }

    @Override
    protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
        DefaultResourcePack defaultResourcePack = MinecraftClient.getInstance().getDefaultResourcePack();
        InputSupplier<InputStream> inputSupplier = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, MCCIC_LOGO);
        if (inputSupplier == null) {
            return new ResourceTexture.TextureData(new FileNotFoundException(MCCIC_LOGO.toString()));
        } else {
            try {
                InputStream stream = inputSupplier.get();

                ResourceTexture.TextureData textureData;
                try {
                    textureData = new ResourceTexture.TextureData(new TextureResourceMetadata(true, true), NativeImage.read(stream));
                } catch (Throwable throwable) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable var7) {
                            throwable.addSuppressed(var7);
                        }
                    }

                    throw throwable;
                }

                if (stream != null) {
                    stream.close();
                }

                return textureData;
            } catch (IOException exception) {
                return new ResourceTexture.TextureData(exception);
            }
        }
    }
}
