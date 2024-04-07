package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean isBeingUsed = false;

    private String vertexSrc;
    private String fragmentSrc;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'patern'
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find the second pattern after #type 'patern'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equalsIgnoreCase("vertex")) {
                vertexSrc = splitString[1];
            }
            else if(firstPattern.equalsIgnoreCase("fragment")) {
                fragmentSrc = splitString[1];
            }
            else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if(secondPattern.equalsIgnoreCase("vertex")) {
                vertexSrc = splitString[2];
            }
            else if(secondPattern.equalsIgnoreCase("fragment")) {
                fragmentSrc = splitString[2];
            }
            else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open shader file: " + filepath;
        }
    }

    public void compile() {
        // Compile and link the shader
        int vertexID, fragmentID;

        // Load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);

        // Check for shader compilation errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);

        // Check for shader compilation errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tShader linking failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        if(!isBeingUsed) {
            glUseProgram(shaderProgramID);
            isBeingUsed = true;
        }
    }

    public void detach() {

        glUseProgram(0);
        isBeingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }
    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }
    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
}
