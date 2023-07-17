const auth = require("../middleware/auth.js");
const router = require("express").Router();
const productReportController = require("../controllers/productReportController.js");

router.post("/:id", auth.verifyUser, productReportController.report_recipe);

module.exports = router;
