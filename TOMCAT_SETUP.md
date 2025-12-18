# Tomcat Configuration Guide

## AJP Connector Error Fix

If you're getting the error: **"AJP Connector node not found: set up one in the server.xml"**

### Option 1: Ignore AJP (Recommended for Simple Setup)

**You don't need AJP connector** if you're running Tomcat standalone. The error can be safely ignored if:
- You're accessing your app directly via `http://localhost:8080`
- You're not using Apache HTTP Server in front of Tomcat
- You're developing/testing locally

**Solution:** Just use the standard HTTP connector (port 8080) which is already configured by default.

---

### Option 2: Add AJP Connector (If You Need Apache HTTP Server Integration)

If you need AJP connector (for Apache HTTP Server integration), follow these steps:

1. **Locate Tomcat's server.xml**
   - Path: `C:\xampp\tomcat\conf\server.xml` (if using XAMPP)
   - Or: `[TOMCAT_INSTALL_DIR]\conf\server.xml`

2. **Find the `<Service name="Catalina">` section** and add the AJP connector:

```xml
<!-- Add this inside <Service name="Catalina">, after the HTTP connector -->
<Connector protocol="AJP/1.3"
           port="8009"
           redirectPort="8443"
           address="127.0.0.1"
           secretRequired="false" />
```

3. **Complete example** (showing both HTTP and AJP connectors):

```xml
<Service name="Catalina">
    <!-- HTTP Connector (port 8080) -->
    <Connector port="8080" 
               protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
    
    <!-- AJP Connector (port 8009) -->
    <Connector protocol="AJP/1.3"
               port="8009"
               redirectPort="8443"
               address="127.0.0.1"
               secretRequired="false" />
    
    <!-- Engine and other configurations... -->
</Service>
```

4. **Restart Tomcat** after making changes.

---

### Option 3: Disable AJP Requirement (If Using IDE)

If you're using IntelliJ IDEA or Eclipse and getting this error:

1. **IntelliJ IDEA:**
   - Go to Run → Edit Configurations
   - Select your Tomcat configuration
   - Remove or disable AJP connector requirement
   - Use only HTTP connector (port 8080)

2. **Eclipse:**
   - Right-click on server → Properties
   - Uncheck "Use AJP Connector" if available

---

## Standard Tomcat Setup (No AJP Needed)

For this Food Delivery application, you only need:

1. **HTTP Connector** (port 8080) - Already configured by default
2. **Access your app:** `http://localhost:8080/food-delivery/`

**No AJP connector required!** The error can be safely ignored.

---

## Quick Test

After setup, test your application:
- Homepage: `http://localhost:8080/food-delivery/`
- Test DB: `http://localhost:8080/food-delivery/test-db`
- Register: `http://localhost:8080/food-delivery/register`
- Login: `http://localhost:8080/food-delivery/login`
- Restaurants: `http://localhost:8080/food-delivery/restaurants`

