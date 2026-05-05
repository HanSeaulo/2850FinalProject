fetch("/current-user")
  .then(res => res.text())
  .then(name => {
    const signinLink = document.getElementById("signin-link");
    const logoutForm = document.getElementById("logout-form");
    const userName = document.getElementById("user-name");

    if (!signinLink || !logoutForm || !userName) return;

    if (name.trim() !== "") {
      signinLink.style.display = "none";
      userName.style.display = "inline";
      userName.textContent = name;
      logoutForm.style.display = "inline";
    } else {
      signinLink.style.display = "inline";
      userName.style.display = "none";
      logoutForm.style.display = "none";
    }
  });