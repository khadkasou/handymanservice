package com.nepbrain.technology.bucket.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service for adding watermarks to images.
 * Supports JPEG and PNG formats.
 */
@Service
@Slf4j
public class WatermarkService {

    private static final String WATERMARK_TEXT = "Tendernotice";
    private static final String LOGO_PATH = "logo.png";
    private static final float WATERMARK_OPACITY = 0.5f; // 50% opacity
    private static final int WATERMARK_FONT_SIZE = 48;
    private static final Color WATERMARK_COLOR = Color.WHITE;
    
    private BufferedImage logoImage;
    
    /**
     * Loads the logo image from resources on initialization.
     */
    private void loadLogo() {
        if (logoImage == null) {
            try {
                Resource resource = new ClassPathResource(LOGO_PATH);
                if (resource.exists()) {
                    try (InputStream inputStream = resource.getInputStream()) {
                        logoImage = ImageIO.read(inputStream);
                        if (logoImage != null) {
                            log.info("✅ Logo loaded successfully from {}", LOGO_PATH);
                        } else {
                            log.warn("⚠️ Failed to read logo image from {}", LOGO_PATH);
                        }
                    }
                } else {
                    log.warn("⚠️ Logo file not found: {}", LOGO_PATH);
                }
            } catch (IOException e) {
                log.error("❌ Error loading logo image: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Adds a watermark to an image if it's a supported image format.
     * 
     * @param imageBytes The original image bytes
     * @param contentType The content type of the image (e.g., "image/jpeg", "image/png")
     * @return Watermarked image bytes, or original bytes if watermarking fails or format is not supported
     */
    public byte[] addWatermark(byte[] imageBytes, String contentType) {
        if (imageBytes == null || imageBytes.length == 0) {
            log.warn("⚠️ Cannot watermark: image bytes are null or empty");
            return imageBytes;
        }

        // Only watermark image files
        if (!isImageType(contentType)) {
            log.debug("Skipping watermark for non-image type: {}", contentType);
            return imageBytes;
        }

        try {
            // Read the image
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage originalImage = ImageIO.read(bais);
            
            if (originalImage == null) {
                log.warn("⚠️ Failed to read image for watermarking");
                return imageBytes;
            }

            // Create a copy of the image with alpha channel support
            BufferedImage watermarkedImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Draw the original image
            Graphics2D g2d = watermarkedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, null);

            // Load logo if not already loaded
            loadLogo();

            // Set watermark properties
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, WATERMARK_OPACITY));
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate watermark dimensions
            int watermarkWidth;
            int watermarkHeight;
            
            if (logoImage != null) {
                // Use logo dimensions, scaled based on image size
                int logoSize = calculateWatermarkSize(originalImage.getWidth(), originalImage.getHeight());
                watermarkWidth = logoSize;
                watermarkHeight = (int) ((double) logoImage.getHeight() / logoImage.getWidth() * logoSize);
            } else {
                // Fallback to text dimensions
                g2d.setColor(WATERMARK_COLOR);
                Font watermarkFont = new Font("Arial", Font.BOLD, calculateFontSize(originalImage.getWidth(), originalImage.getHeight()));
                g2d.setFont(watermarkFont);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                FontMetrics fontMetrics = g2d.getFontMetrics();
                watermarkWidth = fontMetrics.stringWidth(WATERMARK_TEXT);
                watermarkHeight = fontMetrics.getHeight();
            }

            // Draw center watermark (diagonal from bottom-left to top-right, -45 degrees)
            int centerX = originalImage.getWidth() / 2;
            int centerY = originalImage.getHeight() / 2;
            drawTiltedWatermark(g2d, centerX, centerY, -45, watermarkWidth, watermarkHeight);

            // Draw top-left area watermark (upper middle section, parallel to center at -45 degrees)
            int topLeftX = (int) (originalImage.getWidth() * 0.25); // 25% from left
            int topLeftY = (int) (originalImage.getHeight() * 0.25); // 25% from top
            drawTiltedWatermark(g2d, topLeftX, topLeftY, -45, watermarkWidth, watermarkHeight);

            // Draw bottom-right area watermark (lower middle section, parallel to center at -45 degrees)
            int bottomRightX = (int) (originalImage.getWidth() * 0.75); // 75% from left
            int bottomRightY = (int) (originalImage.getHeight() * 0.75); // 75% from top
            drawTiltedWatermark(g2d, bottomRightX, bottomRightY, -45, watermarkWidth, watermarkHeight);

            g2d.dispose();

            // Convert back to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String format = getImageFormat(contentType);
            ImageIO.write(watermarkedImage, format, baos);
            
            byte[] watermarkedBytes = baos.toByteArray();
            log.info("✅ Watermark '{}' added successfully to {} image ({} bytes -> {} bytes)", 
                    WATERMARK_TEXT, contentType, imageBytes.length, watermarkedBytes.length);
            
            return watermarkedBytes;

        } catch (IOException e) {
            log.error("❌ Error adding watermark to image: {}", e.getMessage(), e);
            // Return original bytes if watermarking fails
            return imageBytes;
        } catch (Exception e) {
            log.error("❌ Unexpected error during watermarking: {}", e.getMessage(), e);
            return imageBytes;
        }
    }

    /**
     * Checks if the content type is a supported image format.
     */
    private boolean isImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.equals("image/jpeg") || 
               contentType.equals("image/jpg") || 
               contentType.equals("image/png");
    }

    /**
     * Gets the image format string for ImageIO.write().
     */
    private String getImageFormat(String contentType) {
        if (contentType == null) {
            return "png";
        }
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            default -> "png";
        };
    }

    /**
     * Draws a tilted watermark (logo or text) at the specified position.
     * 
     * @param g2d Graphics2D context
     * @param x X position
     * @param y Y position
     * @param angleDegrees Rotation angle in degrees
     * @param watermarkWidth Width of the watermark
     * @param watermarkHeight Height of the watermark
     */
    private void drawTiltedWatermark(Graphics2D g2d, int x, int y, double angleDegrees, int watermarkWidth, int watermarkHeight) {
        // Save the current transform
        AffineTransform originalTransform = g2d.getTransform();
        
        // Calculate rotation angle in radians
        double angle = Math.toRadians(angleDegrees);
        
        // Create rotation transform: translate to position, rotate, then adjust for centering
        AffineTransform rotationTransform = new AffineTransform();
        rotationTransform.translate(x, y);
        rotationTransform.rotate(angle);
        // Adjust for watermark dimensions to center it properly
        rotationTransform.translate(-watermarkWidth / 2, watermarkHeight / 4);
        
        // Apply the rotation transform
        g2d.setTransform(rotationTransform);
        
        // Draw watermark (logo or text)
        if (logoImage != null) {
            // Draw logo image, scaled to watermark dimensions
            g2d.drawImage(logoImage, 0, 0, watermarkWidth, watermarkHeight, null);
        } else {
            // Fallback to text watermark
            g2d.setColor(WATERMARK_COLOR);
            Font watermarkFont = new Font("Arial", Font.BOLD, calculateFontSize(
                    (int)(watermarkWidth * 1.5), (int)(watermarkHeight * 1.5)));
            g2d.setFont(watermarkFont);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.drawString(WATERMARK_TEXT, 0, 0);
        }
        
        // Restore original transform
        g2d.setTransform(originalTransform);
    }
    
    /**
     * Calculates appropriate watermark size based on image dimensions.
     */
    private int calculateWatermarkSize(int imageWidth, int imageHeight) {
        // Base size on image dimensions (approximately 15% of smaller dimension)
        int baseSize = Math.min(imageWidth, imageHeight) / 7;
        // Multiply by 3 to make logo 3 times larger
        int logoSize = baseSize * 3;
        // Ensure reasonable minimum and maximum sizes
        return Math.max(150, Math.min(logoSize, 900));
    }

    /**
     * Calculates appropriate font size based on image dimensions.
     */
    private int calculateFontSize(int imageWidth, int imageHeight) {
        // Base font size on image dimensions (approximately 5% of average dimension)
        int baseSize = Math.min(imageWidth, imageHeight) / 20;
        // Ensure minimum and maximum sizes
        return Math.max(24, Math.min(baseSize, WATERMARK_FONT_SIZE));
    }
}

