#version 330

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

out vec4 fragColor;

void main() {

    vec4 color = texture(InSampler, texCoord);

    // Convert to grayscale (perceptual luminance)
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    // Slight curve to preserve contrast
    gray = pow(gray, 0.95);

    // Cyan ticket-style tone
    vec3 cyanTone = vec3(0.45, 0.75, 0.80);

    // Apply tone while preserving brightness
    vec3 finalColor = gray * cyanTone;

    fragColor = vec4(finalColor, 1.0);
}