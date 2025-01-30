{
  description = "A Nix-flake-based Java development environment";

  inputs.nixpkgs.url = "https://flakehub.com/f/NixOS/nixpkgs/*.tar.gz";

  outputs = { self, nixpkgs }:
    let
      javaVersion = 23; # Change this value to update the whole stack

      supportedSystems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];
      forEachSupportedSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f {
        pkgs = import nixpkgs { inherit system; overlays = [ self.overlays.default ]; };
      });
    in
    {
      overlays.default =
        final: prev: rec {
          jdk = prev."jdk${toString javaVersion}";
          maven = prev.maven.override { jdk_headless = jdk; };
          gradle = prev.gradle.override { java = jdk; };
        };

      devShells = forEachSupportedSystem ({ pkgs }: {
        default = pkgs.mkShell {
          packages = with pkgs; [
            gcc
            gradle
            temurin-bin
            maven
            ncurses
            patchelf
            zlib
            podman
            quarkus

          ];

          shellHook = ''
            mkdir -p "./.direnv/current"
            ln -sfn ${pkgs.temurin-bin} "./.direnv/current/jdk"

          '';

          PLAYWRIGHT_BROWSERS_PATH = "${pkgs.playwright-driver.browsers}";
          PLAYWRIGHT_SKIP_VALIDATE_HOST_REQUIREMENTS = "true";
          PLAYWRIGHT_NODEJS_PATH = "${pkgs.nodejs-18_x}/bin/node";
          PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = "true";

          nativeBuildInputs = with pkgs; [
            playwright-driver.browsers
            playwright
            nodejs-18_x
          ];
        };
      });
    };
}
