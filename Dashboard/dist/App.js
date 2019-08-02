"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const DependencyInjection_1 = require("./DependencyInjection");
const express = require("express");
const bodyParser = require("body-parser");
class App {
    constructor() {
        this.express = express();
        this.router = express.Router();
        this.config();
    }
    run(io) {
        return __awaiter(this, void 0, void 0, function* () {
            this.dependencyInjection = new DependencyInjection_1.DependencyInjection();
            this.logger = this.dependencyInjection.createLogger();
            this.dashboardEndpoint = this.dependencyInjection.createDashboardEndpoint(io);
            this.dashboardEndpoint.routes(this.router);
        });
    }
    config() {
        // support application/json type post data
        let bodyParserCustom = {
            json: { limit: '50mb', extended: true },
            urlencoded: { limit: '50mb', extended: true }
        };
        this.express.use(bodyParser.json({
            limit: '50mb'
        }));
        this.express.use(bodyParser.text({
            limit: '50mb'
        }));
        //support application/x-www-form-urlencoded post data
        this.express.use(bodyParser.urlencoded({
            limit: '50mb',
            extended: true,
            parameterLimit: 1000000
        }));
        this.express.use(express.static('public'));
    }
}
exports.App = App;
//# sourceMappingURL=App.js.map