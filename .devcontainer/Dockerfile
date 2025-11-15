# Android Development Container with Android Studio
FROM ubuntu:22.04

# Verhindere interaktive Package-Installation + English Locale
ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8

# System-Updates und grundlegende Tools (mit Progress-Output)
RUN apt-get update && echo "📦 Installing system packages..." && apt-get install -y -q \
    curl \
    wget \
    unzip \
    git \
    build-essential \
    file \
    apt-transport-https \
    ca-certificates \
    gnupg \
    lsb-release \
    software-properties-common \
    locales \
    sudo \
    && echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
    && locale-gen \
    && echo "✅ System packages installed!" \
    && rm -rf /var/lib/apt/lists/*

# Non-root User erstellen
RUN echo "👤 Creating vscode user..." \
    && groupadd --gid 1000 vscode \
    && useradd --uid 1000 --gid 1000 -m -s /bin/bash vscode \
    && echo "vscode ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/vscode \
    && chmod 0440 /etc/sudoers.d/vscode \
    && echo "✅ User vscode created!"

# Java 21 installieren (mit Progress)
RUN echo "☕ Installing Java 21..." && apt-get update && apt-get install -y -q openjdk-21-jdk \
    && echo "✅ Java 21 installed: $(java -version 2>&1 | head -1)" \
    && rm -rf /var/lib/apt/lists/*

# Umgebungsvariablen setzen
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin

# Android SDK Verzeichnis
ENV ANDROID_SDK_ROOT=/android-sdk
ENV ANDROID_HOME=/android-sdk
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools

# Android Command Line Tools installieren (mit Progress)
RUN echo "📱 Installing Android SDK..." && mkdir -p $ANDROID_SDK_ROOT/cmdline-tools \
    && cd $ANDROID_SDK_ROOT/cmdline-tools \
    && echo "⬇️  Downloading Android Command Line Tools..." \
    && wget --progress=bar:force https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && echo "📦 Extracting..." \
    && unzip -q commandlinetools-linux-*_latest.zip \
    && mv cmdline-tools latest \
    && rm commandlinetools-linux-*_latest.zip \
    && echo "✅ Android Command Line Tools installed!"

# Android SDK Components installieren (mit Progress)
RUN echo "📱 Installing Android SDK components..." \
    && yes | $ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --verbose \
    "platforms;android-36" \
    "build-tools;36.0.0" \
    "platform-tools" \
    "tools" \
    && echo "✅ Android SDK components installed!" \
    && chown -R vscode:vscode $ANDROID_SDK_ROOT

# Android Studio installieren (mit Progress)
RUN echo "🚀 Installing Android Studio..." && cd /opt \
    && echo "⬇️  Downloading Android Studio (~1.5GB)..." \
    && wget --progress=bar:force https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2025.1.3.7/android-studio-2025.1.3.7-linux.tar.gz \
    && echo "📦 Extracting Android Studio..." \
    && tar -xzf android-studio-2025.1.3.7-linux.tar.gz \
    && rm android-studio-2025.1.3.7-linux.tar.gz \
    && ln -s /opt/android-studio/bin/studio.sh /usr/local/bin/studio \
    && echo "✅ Android Studio installed!"

# GUI-Libraries für Android Studio (NoVNC optimiert)
RUN echo "🖥️  Installing GUI libraries..." && apt-get update && apt-get install -y -q \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfreetype6 \
    fontconfig \
    libgtk-3-0 \
    libxss1 \
    libasound2 \
    dbus-x11 \
    x11-utils \
    && echo "✅ GUI libraries installed!" \
    && rm -rf /var/lib/apt/lists/*

# NoVNC wird vom desktop-lite Feature bereitgestellt
# Kein manuelles Setup nötig - Feature übernimmt VNC Server + noVNC Web Interface

# Workspace Verzeichnis
WORKDIR /workspace

# Port für ADB
EXPOSE 5037

# Als vscode User wechseln
USER vscode

# Standard Command
CMD ["bash"]
