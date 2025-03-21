package project.uevents.management.FCM;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() throws IOException {
        String jsonString = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"uems-4c42d\",\n" +
                "  \"private_key_id\": \"f9aaae91b58ca4c4ef317dac54e6fd5094f2f9e7\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC1WcBRDhHEi997\\nde3uUK5oW3vGnxOf7/qVYvoDZE573rqqItd+LqV9ZEeuJRehSD3KQhIdyjd4Qn8u\\nSuJqAedsb0tU0HxClws9+VWO0pSHZYNF2Lcpqh1Qrh2xQ019WyGm3NM7yUS80L9E\\n1dNQC590NEQlBgtWmsO7hkqRB5eBk86XHNlG1QCzaxLdpu7bp/6+HkMikr9aWOuJ\\nwK2GMRA+oHOGaBwMCXxEhHjHDyK/qvtjH7nMeg3uGar4JhJ/6B77gDo3qLDSmCOd\\n1YtVDJyUdv5YIjC4hETz7A0wWGFiiDhmD3g3f64BKnzaGLkxBynDMoKo+zxE5KIU\\nY47cFVkLAgMBAAECggEAQ/Bjw/npXiCokLAVevpu3FmpfTeLOpfE4MI7t5mUbYN7\\nmtGCuZrwFJHE6NCskZdmzqcB8ncXYM5IpycWu3ALKuxm5FdWtfuwEn+eNeteCXtW\\nOox90fYRFXClXws/tH63lLOVzwgP4Eyz2y3gBFTWX8pU3SnuhXtlaJNezAZ0E+6R\\n+jaCtcr7tqHFnS/f+PPILp980U8/4pEo5t58kzYgwVwVIl4vO4Zcf4uHPUdMg/0y\\n0gmXgAXWpnthipRHKaAVvIBmcJGhh3+hKvQShVv4H243pDWnirJfava8WmZ2HcKI\\n3yKNSXbD4EQSfcpVr0kdxYktKwkR7yel38MAWFOKgQKBgQDkIDUdC3fpATsN7Vlt\\nikzLreijkrmkcMvgso+PlV8KYIzEqkGMmVQxJSmAGcJxsPy3qz+qUIqb1rH3xH93\\nztxA4mWt+y1iEr41CycqKH/T03Hr7MRZUC78BL+okvpnIbfocYmuWfK0eiXi88j5\\nXj4DZFbG0JhHNC7HuokwVVyvcwKBgQDLgmlPf17pd6gXomtlsumTofBFDG1nd9h/\\nHtn++eJqDdK39+SlXkvwuQFQK7Av0ETB2XIFNQ99pBrKlESS31lzMiXHM04oiJ5d\\n56BPDt65SWuZMUEw6HA0sTcpIMyp4Xa9Ng0Kja4zmB5Y+8g/WQM8vWDV6QEksVnL\\nQlIm2piaCQKBgQDG5Z3pePpPWRdYCT0TWKWkYDiZhW/+g6n+ezDYC3AV6CBTx+3m\\n3DifsLsCSXNTFPECYnjb0eVIAmSgld5dYyp+F+Z3Tqw++gCE1viKtUzeL1kATirT\\nhCmy6aEBJUTVbnyeWX+lWB9YY7dsrQjpAtYmtvFB3tUmH8U5ttWuIn7xKQKBgQC4\\nwoYviXi2x06RXoDYjMzono3Pck/lLvYbuvu5NE5tcG1NXj50MB8Lxjq9+Vv0nMW/\\npFWTCrAspg84udFj8mh4YB/SMO8Vd6snoAF3pQ1p9JE+PGxuJaiw/e8RZNjKpoox\\nj9/ZI/Xym3l52eTcNkjtHRXMPsUt9eGK2oRRCAahWQKBgQCyOFC0SaAU1LjsyqjF\\njtlO3f2EJRrwhE7xGqc3FIJzmIoQ37ylbFzwdUnx1tBdkh3umqKFLMiE4Idi1T08\\nRGU/YmJCCVWz9wEsDYZPXg3M6JprF+X8rtaLjmENM9pQQJuKprLMB3x8wEZl6JCp\\nTIlKJOETcL42yOF1pJjk18H1zg==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-fbsvc@uems-4c42d.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"111871675222334239928\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40uems-4c42d.iam.gserviceaccount.com\",\n" +
                "  \"universe_domain\": \"googleapis.com\"\n" +
                "}\n";

        InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        GoogleCredentials googleCredentials;

        {
            googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList(firebaseMessagingScope));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        }

    }

}
