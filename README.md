# disAsterRelief

Contents:
1. Disclaimer
2. Inspiration, Objectives, and Goals
3. Installation
4. User's Guide
-----------------------------------------------------------------------------------------------------------------------

Disclaimer:
########################################################################################################################
#!!! This Project uses Google ARcore and therefore REQUIRES the use of a Samsung Galaxy S8 or Google Pixel 2. These devices #cannot be spoofed, the program must be run on the physical device. Due to identifiers in Gradle files being machine #dependent, it is diffuclt to pull this repo and run the app from it. There is a .apk file provided in the root folder, as #well as submitted in the "source code" portion of the HackerEarth submission for the hackathon. Please install this .apk on #an S8 or Pixel 2 and run from there. !!!
#########################################################################################################################


2. Inspiration, Objectives, and Goals
-------------------------------------
  Inspired by the problems facing Emergency Medical Services (EMS) in our increasingly dangerous world, disAsterRelief takes advantage of Augmented Reality to empower First Responders and maximize their life-saving potential. Using Google’s brand new ARCore (still in developer preview!), location data, and biometrics from typical modern smartphones and wearables, disAsterRelief takes in victim data from Mass Casualty Events and displays it to EMTs in an intuitive and informative interface. 
	The best way to understand the power of disAsterRelief is to imagine it in action. disAsterRelief is designed with minimal distracting interface elements; the app’s entire goal is to seamlessly contextualize the real world. Today, EMS arrives on scene at a Mass Casualty Event and sees chaos. With disAsterRelief, an EMT can open up their smartphone (eventually, this will be ported to an AR headset) and get a data-driven map of nearby victims superimposed on the real word in what is most appropriately deemed merged reality. The map identifies victim location with a color-coded marker and displays the victim’s name, age, and the distance from the EMT to the victim. The color scale indicates a patient’s current status with a well-recognized green-yellow-red categorization (red indicates critical condition). Such data allows an EMT to not only find victims quicker, but easily prioritize and separate victims in critical condition from those who are (relatively) safe. 
	The ultimate goal (and areas for future development) is for this app to transcend the bounds of a mobile device and be implemented in a wearable like Microsoft’s HoloLens or Google Glasses. In this case, the app would still provide all the life-saving information available in the current beta, but it would do so in a hands-free deployment. This allows medical personnel to intake the information as they take other actions. Additional improvements to the framework include using machine learning to assist First Responders in prioritizing victims. Using all victims’ biometric data, and their locations relative to the EMT, the app would be able to recommend which victims should be prioritized. The specifics of this algorithm would require some advances in the data available from wearables and consultation with medical professionals. 
	In a perfect world, this app would have no use. But, as we build toward such a world, disAsterRelief believes that we owe it to First Responders and innocent civilians to use all available technology to effectively respond to disasters. 
 
 
3. Installation
---------------
**Due to the limitations of ARCore, disAsterRelief will ONLY run on a real world Pixel 2 or Samsung S8 (NO EMULATORS)**
We recommend that you use the provided .apk file to install the application on your device and then run from your device. Gradle’s dependencies on the file structure of the computer the app was built on makes it difficult to build on other computers. To install on your device, take the .apk in the root folder of the github repository or the one supplied in the “source code” section of the HackerEarth submission, and move it to your phone. Then open the .apk in your phone’s file system and install.

 
4. User's Guide
---------------
Welcome to disAsterRelief. The goal of this app is to aid EMS and other first responders in finding and rescuing victims of what EMS calls Mass Casualty Events. The app will use augmented reality and GPS data to identify the location and display vital data about nearby event victims.
To use the app, simply launch the app from the app launcher. To initialize the Augemented Reality engine, point your camera at a flat surface, and move your camera right to left slowly until the “Searching for Surface” toaster disappears. As soon as the engine recognizes a surface, the app will populate with victim data and locations calculated relative to your current location using GPS data. Now you can move around your environment and begin locating and rescuing victims. 

