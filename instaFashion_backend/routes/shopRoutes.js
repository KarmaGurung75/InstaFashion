const router = require("express").Router();
const shopController = require("../controllers/shopController.js");
const auth = require("../middleware/auth.js");

router.post("/", auth.verifyUser, shopController.add_shop);

router.get("/:id", auth.verifyUser, shopController.get_shop_details);

router.patch(
  "/cover-image/:id",
  auth.verifyUser,
  shopController.update_shop_cover_image
);

router.patch(
  "/restaurant-image/:id",
  auth.verifyUser,
  shopController.update_shop_image
);

router.patch(
  "/no-image/:id",
  auth.verifyUser,
  shopController.edit_shop_without_image
);

router.delete("/:id", auth.verifyUser, shopController.delete_shop);

module.exports = router;
