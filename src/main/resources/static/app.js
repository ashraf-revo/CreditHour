'use strict';

angular.module('revolovexApp', ['LocalStorageModule',
        'ngResource', 'ui.router', 'ngCookies', 'ngCacheBuster', 'ui.bootstrap', 'dialogs.main', 'ui-notification', 'ngSanitize'])
    .constant("cdnLocation", $("meta[property='cdnLocation']").attr('content'))
    .run(function ($rootScope, $window, $http, $state, Auth, Principal, cdnLocation, $sce) {


        $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
            $rootScope.toState = toState;
            $rootScope.toStateParams = toStateParams;

            if (Principal.isIdentityResolved()) {
                Auth.authorize();
            }
        });

        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
            var titleKey = 'any tilte ';
            if ((toState.name == 'login' ) && Principal.isAuthenticated()) {
                $state.go('home');
            }

            $rootScope.previousStateName = fromState.name;
            $rootScope.previousStateParams = fromParams;

            if (toState.data.pageTitle) {
                titleKey = toState.data.pageTitle;
            }

            $window.document.title = titleKey;
        });
    })

    .config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, httpRequestInterceptorCacheBusterProvider) {
        //enable CSRF
        $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
        $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        $urlRouterProvider.otherwise('/');
        $stateProvider.state('site', {
            'abstract': true,
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ]
            }
        });
    }).config(['dialogsProvider', 'NotificationProvider', function (dialogsProvider, NotificationProvider) {
    dialogsProvider.useBackdrop('static');
    dialogsProvider.useEscClose(false);
    dialogsProvider.useCopy(false);
    dialogsProvider.setSize('lg');
    NotificationProvider.setOptions({
        delay: 10000,
        startTop: 20,
        startRight: 10,
        verticalSpacing: 20,
        horizontalSpacing: 20,
        positionX: 'left',
        positionY: 'bottom'
    });
}]).directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function () {
                scope.$apply(function () {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

'use strict';


angular.module('revolovexApp')
    .controller('LoginController', function ($rootScope, $scope, $state, Auth, Notification) {
        $scope.user = {};
        $scope.errors = {};

        $scope.rememberMe = true;
        $scope.submit = function () {
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            }).then(function () {
                $state.go('home');
            }).catch(function () {
                Notification.error({message: 'Authentication failed!', positionY: 'bottom', positionX: 'left'});
            });
        };
    });


'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('login', {
                parent: 'site',
                url: '/',
                data: {
                    roles: [],
                    pageTitle: 'login'
                },
                views: {
                    'content@': {
                        templateUrl: cdnLocation + 'content/login.html',
                        controller: 'LoginController'
                    }
                }
            });
    });

'use strict';

angular.module('revolovexApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });

'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('logout', {
                parent: 'site',
                url: '/logout',
                data: {
                    roles: []
                },
                views: {
                    'content@': {
                        templateUrl: cdnLocation + 'content/main.html',
                        controller: 'LogoutController'
                    }
                }
            });
    });

'use strict';

angular.module('revolovexApp')
    .controller('RegisterController', function ($scope, Auth, Notification) {
        $scope.submit = function () {
            Auth.createAccount($scope.form)
                .then(function () {
                    Notification.success({
                        message: 'Success Please Check Your Email',
                        positionY: 'bottom',
                        positionX: 'left'
                    });
                }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });

        };
    });


'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('register', {
                parent: 'site',
                url: '/register',
                data: {
                    authorities: [],
                    pageTitle: 'register'
                },
                views: {
                    'content@': {
                        templateUrl: cdnLocation + 'content/register.html',
                        controller: 'RegisterController'
                    }
                }
            });
    });

'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('error', {
                parent: 'site',
                url: '/error',
                data: {
                    roles: [],
                    pageTitle: 'Error'
                },
                views: {
                    'content@': {
                        templateUrl: cdnLocation + 'content/error.html'
                    }
                }
            })
            .state('accessdenied', {
                parent: 'site',
                url: '/accessdenied',
                data: {
                    roles: [],
                    pageTitle: 'Access Denied'
                },
                views: {
                    'content@': {
                        templateUrl: cdnLocation + 'content/accessdenied.html'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .controller('ActivationController', function ($scope, $stateParams, Auth, Notification) {
        Auth.activateAccount({key: $stateParams.key}).then(function () {
            $scope.error = null;
            $scope.success = 'OK';
        }).catch(function () {
            $scope.success = null;
            $scope.error = 'ERROR';
            Notification.error({message: 'Error', positionY: 'bottom', positionX: 'left'});
        }).then(function () {
            Notification.success({message: 'Success', positionY: 'bottom', positionX: 'left'});
        });
    });


'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('activate', {
                parent: 'site',
                url: '/activate?key',
                data: {
                    authorities: [],
                    pageTitle: 'Activate'
                },
                views: {
                    'content@': {
                        templateUrl: cdnLocation + 'content/activate.html',
                        controller: 'ActivationController'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('home', {
                url: '/home',
                data: {
                    roles: ['ROLE_AUTHENTICATED'],
                    pageTitle: "Welcome"
                },
                views: {
                    'content@': {
                        templateUrl: cdnLocation + 'content/home.html',
                        controller: 'HomeController'

                    },
                    'homeNavbar@home': {
                        templateUrl: cdnLocation + 'content/homeNavbar.html'
                    },
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/homeContent.html'
                    }
                }, parent: 'site'
            });
    });


'use strict';

angular.module('revolovexApp')
    .controller('HomeController', function ($scope, $state, Principal, Auth, cdnLocation) {
        $scope.cdnLocation = cdnLocation;
        Principal.identity().then(function (account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        $scope.logout = function () {
            Auth.logout();
            $state.go('login');
        };
    });


'use strict';

angular.module('revolovexApp')
    .controller('AvailableTermController', function ($scope, $http, dialogs, cdnLocation, Notification, $sce) {
        $http.get(document.location.origin + '/api/student/pt')
            .success(function (pt) {
                $scope.pt = pt;
            });
        $scope.load = function (pt) {
            $http.get(document.location.origin + '/api/student/pt/' + pt.id)
                .success(function (one) {
                    var dlg = dialogs.create($sce.trustAsResourceUrl(cdnLocation + 'content/updatept.html'), 'pt', one);

                });
        };
    }).controller('pt', function ($scope, $uibModalInstance, data, $http, Notification) {
        $scope.data = angular.copy(data);
        $scope.cancel = function () {
            $uibModalInstance.dismiss();
        };
        $scope.save = function () {
            for (var i = $scope.data.ps.length - 1; i >= 0; i--) {
                if ($scope.data.ps[i].subject.selected != true) {
                    $scope.data.ps.splice(i, 1);
                }
            }
            $http.post(document.location.origin + '/api/student/pt', $scope.data)
                .success(function (out) {
                }).then(function () {
                $uibModalInstance.close();
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                $scope.data = angular.copy(data);
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };
    }
);


'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('availableterm', {
                parent: 'home',
                url: '/availableterm',
                data: {
                    roles: ['ROLE_STUDENT'],
                    pageTitle: 'AvailableTerm'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/availableterm.html',
                        controller: 'AvailableTermController'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .controller('AvailableSubjectController', function ($scope, $http) {
        $http.get(document.location.origin + '/api/student/subject')
            .success(function (subject) {
                $scope.subject = subject;
            });
    });


'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('availablesubject', {
                parent: 'home',
                url: '/availablesubject',
                data: {
                    roles: ['ROLE_STUDENT'],
                    pageTitle: 'AvailableSubject'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/availablesubject.html',
                        controller: 'AvailableSubjectController'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .controller('ProfileController', function ($scope, $http) {
        $http.get(document.location.origin + '/api/student/student')
            .success(function (student) {
                $scope.student = student;
            });
        $scope.hours = function () {
            var hours = 0;
            if (angular.isDefined($scope.student))
                for (var x = 0; x < $scope.student.pt.length; x++) {
                    for (var k = 0; k < $scope.student.pt[x].ps.length; k++) {
                        if ($scope.student.pt[x].ps[k].state == "success") hours += $scope.student.pt[x].ps[k].subject.hour
                    }
                }
            return hours;
        };
    });


'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('profile', {
                parent: 'home',
                url: '/profile',
                data: {
                    roles: ['ROLE_STUDENT'],
                    pageTitle: 'Profile'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/profile.html',
                        controller: 'ProfileController'
                    }
                }
            });
    });


'use strict';
angular.module('revolovexApp')
    .controller('SettingsController', function ($scope, $http, dialogs, $window, Notification) {
        $scope.lastPaymentPlus1 = function (last) {
            if (last == 0)last = new Date();
            last = new Date(last);
            last.setFullYear(last.getFullYear() + 1);
            return last;
        };
        $http.get(document.location.origin + '/api/admin/admin')
            .success(function (admin) {
                    $scope.admin = admin;
                    $scope.admin.oldplane = admin.plane;
                    $scope.admin.lastPaymentPlus1 = $scope.lastPaymentPlus1($scope.admin.lastPayment);

                    $scope.minPlane = function () {
                        return Math.ceil($scope.admin.studentCount / $scope.admin.rate);
                    };
                    /**
                     * @return {boolean}
                     */
                    $scope.IsScaleDisable = function () {
                        if ($scope.IsNewYear())
                            return false;
                        else
                            return $scope.admin.oldplane != $scope.admin.plane;
                    };


                    /**
                     * @return {boolean}
                     */
                    $scope.IsNewYear = function () {
                        return new Date().getTime() > $scope.admin.lastPaymentPlus1.getTime();
                    };

                    /**
                     * @return {number}
                     */
                    $scope.count = function () {
                        var number = new Date().getDate() - $scope.admin.lastPaymentPlus1.getDate();
                        return number;
                    };

                    /**
                     * @return {number}
                     */
                    $scope.ScaleCost = function () {
                        if ($scope.admin.plane < $scope.admin.oldplane) {
                            return 0;
                        }
                        else {
                            return $scope.admin.cost * $scope.admin.plane - $scope.admin.cost * $scope.admin.oldplane;
                        }
                    };
                    /**
                     * @return {number}
                     */
                    $scope.YearCost = function () {
                        return $scope.admin.cost * $scope.admin.plane;
                    };

                    $scope.pay = function (method) {
                        $http.post(document.location.origin + '/api/admin/admin/' + method, {"plane": $scope.admin.plane})
                            .success(function (result) {
                                if (angular.isDefined(result.links))
                                    if (result.links.length >= 0)
                                        for (var i = 0; i < result.links.length; i++) {
                                            if (result.links[i].rel == "approval_url") {
                                                $window.location.href = result.links[i].href;
                                            }
                                        }
                                    else $window.location.reload();
                                else $window.location.reload();
                            }).then(function () {
                            Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
                        }).catch(function (e) {
                            Notification.error({
                                message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                                positionY: 'bottom',
                                positionX: 'left'
                            });
                        });
                    };
                }
            );
    });

'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('settings', {
                parent: 'home',
                url: '/settings',
                data: {
                    roles: ['ROLE_ADMIN', 'ROLE_SETTINGS'],
                    pageTitle: 'Settings'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/settings.html',
                        controller: 'SettingsController'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .controller('OneStudentController', function ($scope, $http, dialogs, $stateParams, cdnLocation, Notification, $sce) {
        $http.get(document.location.origin + '/api/admin/student/' + $stateParams.id)
            .success(function (student) {
                $scope.student = student;
                $scope.hours = function () {
                    var hours = 0;
                    if (angular.isDefined($scope.student))
                        for (var x = 0; x < $scope.student.pt.length; x++) {
                            for (var k = 0; k < $scope.student.pt[x].ps.length; k++) {
                                if ($scope.student.pt[x].ps[k].state == "success") hours += $scope.student.pt[x].ps[k].subject.hour
                            }
                        }
                    return hours;
                };

            });


        $scope.delete = function (ps) {
            $http.delete(document.location.origin + "/api/admin/ps/" + ps.id).success(function () {
                for (var i = $scope.student.pt.length - 1; i >= 0; i--) {
                    for (var j = $scope.student.pt[i].ps.length - 1; j >= 0; j--) {
                        if ($scope.student.pt[i].ps[j].id == ps.id) {
                            $scope.student.pt[i].ps.splice(j, 1);
                        }
                    }
                }
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };
        $scope.update = function (ps) {


            $http.get(document.location.origin + "/api/admin/ps/" + ps.id).success(function (data) {
                var dlg = dialogs.create($sce.trustAsResourceUrl(cdnLocation + 'content/updateps.html'), 'updatePs', data, {size: 'lg'});
                dlg.result.then(function (result) {
                    for (var i = $scope.student.pt.length - 1; i >= 0; i--) {
                        for (var j = $scope.student.pt[i].ps.length - 1; j >= 0; j--) {
                            if ($scope.student.pt[i].ps[j].id == ps.id) {
                                $scope.student.pt[i].ps[j] = result;
                            }
                        }
                    }
                });
            });
        };
    })
    .controller('updatePs', function ($scope, $uibModalInstance, data, $http, Notification) {
        $scope.ps = data;
        $scope.cancel = function () {
            $uibModalInstance.dismiss();
        };
        $scope.save = function () {
            $http.post(document.location.origin + "/api/admin/ps/", $scope.ps).success(function (ps) {
                $uibModalInstance.close(ps);
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };
    });


'use strict';

angular.module('revolovexApp')
    .controller('StaticsController', function ($scope, $http, dialogs, $stateParams, cdnLocation, Notification, $sce) {
        $http.get(document.location.origin + '/api/admin/term/statics/' + $stateParams.id)
            .success(function (statics) {
                $scope.statics = statics;
                $scope.term = $stateParams.id;
            });
    });

'use strict';

angular.module('revolovexApp')
    .controller('StudentController', function ($scope, $http, dialogs, $state, cdnLocation, Notification, $sce) {
        var f = 0;
        $scope.term = "";
        $scope.textterm = "";
        $scope.form = {};
        if (!isNaN($state.params.term) || !isNaN($state.params.subject)) {
            if (!isNaN(parseInt($state.params.term)) && !isNaN(parseInt($state.params.subject))) {
                $scope.term = "?term=" + parseInt($state.params.term) + "&subject=" + parseInt($state.params.subject);
                $scope.textterm = "From Term " + parseInt($state.params.term) + " And Subject " + parseInt($state.params.subject);
                $http.get(document.location.origin + '/api/admin/student/?term=' + $state.params.term + '&subject=' + parseInt($state.params.subject))
                    .success(function (student) {
                        $scope.student = student;
                    });
            } else if (!isNaN(parseInt($state.params.term))) {
                $scope.term = "?term=" + parseInt($state.params.term);
                $scope.textterm = "From Term " + parseInt($state.params.term);
                $http.get(document.location.origin + '/api/admin/student/?term=' + $state.params.term)
                    .success(function (student) {
                        $scope.student = student;
                    });
            }
            else f = 1;
        } else f = 1;

        if (f == 1) {
            $http.get(document.location.origin + '/api/admin/student')
                .success(function (student) {
                    $scope.student = student;
                });
        }
        $scope.uplodeStudents = function () {

            var fd = new FormData();
            fd.append('file', $scope.form.file);
            $http.post(document.location.origin + "/api/admin/student/uploadstudents", fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).success(function (student) {
                $scope.form.file = null;
                for (var i = 0; i < student.length; i++)
                    $scope.student.push(student[i]);

            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };
        $scope.save = function () {
            $http.post(document.location.origin + "/api/admin/student", $scope.form).success(function (student) {
                if ($scope.form.id == null) {
                    $scope.form = {};
                    $scope.student.push(student);
                }
                else {
                    $scope.form = {};
                }
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };

        $scope.delete = function (student) {
            $http.delete(document.location.origin + "/api/admin/student/" + student.id + $scope.term).success(function () {
                for (var i = $scope.student.length - 1; i >= 0; i--) {
                    if ($scope.student[i].id == student.id) {
                        $scope.student.splice(i, 1);
                    }
                }
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };
        $scope.update = function (student) {
            $http.get(document.location.origin + "/api/admin/student/" + student.id).success(function (data) {
                var dlg = dialogs.create($sce.trustAsResourceUrl(cdnLocation + 'content/updatestudent.html'), 'updatestudent', data);
                dlg.result.then(function (result) {
                    for (var i = $scope.student.length - 1; i >= 0; i--) {
                        if ($scope.student[i].id == result.id) {
                            $scope.student[i] = result;
                        }
                    }

                });
            });
        };
    }).controller('updatestudent', function ($scope, $uibModalInstance, data, $http, Notification) {
    $scope.form = data;
    $scope.cancel = function () {
        $uibModalInstance.dismiss();
    };
    $scope.save = function () {
        $http.post(document.location.origin + "/api/admin/student", $scope.form).success(function (student) {
            $uibModalInstance.close(student);
        }).then(function () {
            Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
        }).catch(function (e) {
            Notification.error({
                message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                positionY: 'bottom',
                positionX: 'left'
            });
        });
    };
});

'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('student', {
                parent: 'home',
                url: '/student',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'student'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/student.html',
                        controller: 'StudentController'
                    }
                }
            }).state('studentterm', {
            parent: 'student',
            url: '/:term'
        }).state('studenttermsubject', {
                parent: 'student',
                url: '/:term/:subject'
            })
            .state('onestudent', {
                parent: 'home',
                url: '/onestudent/:id',
                data: {
                    roles: ['ROLE_AUTHENTICATED', 'ROLE_ADMIN'],
                    pageTitle: 'student'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/onestudent.html',
                        controller: 'OneStudentController'
                    }
                }
            });
    });
'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('statics', {
                parent: 'home',
                url: '/statics/:id',
                data: {
                    roles: ['ROLE_AUTHENTICATED', 'ROLE_ADMIN'],
                    pageTitle: 'student'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/statics.html',
                        controller: 'StaticsController'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .controller('SubjectController', function ($scope, $http, dialogs, cdnLocation, Notification, $sce) {
        $http.get(document.location.origin + '/api/admin/subject/')
            .success(function (subject) {
                $scope.subject = subject;
            });
        $scope.form = {hour: 3, maxGrade: 100};
        $scope.save = function () {
            $http.post(document.location.origin + "/api/admin/subject", $scope.form).success(function (subject) {
                $scope.subject.push(subject);
                $scope.form = {hour: 3, maxGrade: 100};
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });

        };
        $scope.delete = function (subject) {
            $http.delete(document.location.origin + "/api/admin/subject/" + subject.id).success(function () {
                for (var i = $scope.subject.length - 1; i >= 0; i--) {
                    if ($scope.subject[i].id == subject.id) {
                        $scope.subject.splice(i, 1);
                    }
                }
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };
        $scope.update = function (subject) {
            $http.get(document.location.origin + "/api/admin/subject/" + subject.id).success(function (data) {
                var dlg = dialogs.create($sce.trustAsResourceUrl(cdnLocation + 'content/updatesubject.html'), 'updatesubject', data);
                dlg.result.then(function (result) {
                    for (var i = $scope.subject.length - 1; i >= 0; i--) {
                        if ($scope.subject[i].id == result.id)
                            $scope.subject[i] = result;
                        for (var k = $scope.subject[i].required.length - 1; k >= 0; k--) {
                            if ($scope.subject[i].required[k].id == result.id)
                                $scope.subject[i].required[k] = result;
                        }
                    }
                });
            });
        };
        $scope.change = function (subject) {

            $http.get(document.location.origin + "/api/admin/subject/" + subject.id).success(function (data) {
                var dlg = dialogs.create($sce.trustAsResourceUrl(cdnLocation + 'content/updaterequired.html'), 'updaterequired', {
                    'one': data,
                    'all': $scope.subject
                });
                dlg.result.then(function (result) {
                    for (var i = $scope.subject.length - 1; i >= 0; i--) {
                        if ($scope.subject[i].id == result.id)
                            $scope.subject[i] = result;
                    }
                });
            });
        };

    }).controller('updaterequired', function ($scope, $uibModalInstance, data, $filter, $http, Notification) {
    $scope.form = data.one;
    $scope.all = data.all;
    for (var ki = 0; ki < $scope.all.length; ki++) {
        $scope.all[ki].selected = false;
    }
    for (var i = 0; i < $scope.form.required.length; i++) {
        for (var k = 0; k < $scope.all.length; k++) {
            if ($scope.all[k].id == $scope.form.required[i].id) {
                $scope.all[k].selected = true;
            }
        }
    }
    $scope.selectChange = function () {
        $scope.form.required = $filter('filter')($scope.all, {selected: true});
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss();
    };
    $scope.save = function () {
        $http.put(document.location.origin + "/api/admin/subject/", $scope.form).success(function (subject) {
            $uibModalInstance.close(subject);
        }).then(function () {
            Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
        }).catch(function (e) {
            Notification.error({
                message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                positionY: 'bottom',
                positionX: 'left'
            });
        });
    };
}).controller('updatesubject', function ($scope, $uibModalInstance, data, $http, Notification) {
    $scope.form = data;
    $scope.cancel = function () {
        $uibModalInstance.dismiss();
    };
    $scope.save = function () {
        $http.post(document.location.origin + "/api/admin/subject", $scope.form).success(function (subject) {
            $uibModalInstance.close(subject);
        }).then(function () {
            Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
        }).catch(function (e) {
            Notification.error({
                message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                positionY: 'bottom',
                positionX: 'left'
            });
        });
    };
});

'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('subject', {
                parent: 'home',
                url: '/subject',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'subject'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/subject.html',
                        controller: 'SubjectController'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .controller('TermController', function ($scope, $http, dialogs, cdnLocation, Notification, $sce) {
        $http.get(document.location.origin + '/api/admin/term')
            .success(function (term) {
                $scope.term = term;
            });
        $scope.uplodeGrades = function () {

            var fd = new FormData();
            fd.append('file', $scope.form.file);
            $http.post(document.location.origin + "/api/admin/term/uploadgrades/", fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).success(function (student) {
                $scope.form.file = null;
                for (var i = 0; i < student.length; i++)
                    $scope.student.push(student[i]);

            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };

        $scope.form = {minHour: 15, maxHour: 20, enabled: 'true'};
        $scope.save = function () {
            $http.post(document.location.origin + "/api/admin/term", $scope.form).success(function (term) {
                $scope.term.push(term);
                $scope.form = {minHour: 15, maxHour: 20, enabled: 'true'};
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });

        };
        $scope.delete = function (term) {
            $http.delete(document.location.origin + "/api/admin/term/" + term.id).success(function () {
                for (var i = $scope.term.length - 1; i >= 0; i--) {
                    if ($scope.term[i].id == term.id) {
                        $scope.term.splice(i, 1);
                    }
                }
            }).then(function () {
                Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
            }).catch(function (e) {
                Notification.error({
                    message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                    positionY: 'bottom',
                    positionX: 'left'
                });
            });
        };
        $scope.update = function (term) {
            $http.get(document.location.origin + "/api/admin/term/" + term.id).success(function (data) {
                var dlg = dialogs.create($sce.trustAsResourceUrl(cdnLocation + 'content/updateterm.html'), 'updateterm', data);
                dlg.result.then(function (result) {
                    for (var i = $scope.term.length - 1; i >= 0; i--) {
                        if ($scope.term[i].id == result.id) {
                            $scope.term[i] = result;
                        }
                    }

                });
            });
        };
    }).controller('updateterm', function ($scope, $uibModalInstance, data, $http, Notification) {
    $scope.form = data;
    $scope.form.enabled = String(data.enabled);
    $scope.cancel = function () {
        $uibModalInstance.dismiss();
    };
    $scope.save = function () {
        $http.post(document.location.origin + "/api/admin/term", $scope.form).success(function (term) {
            $uibModalInstance.close(term);
        }).then(function () {
            Notification.success({message: 'success', positionY: 'bottom', positionX: 'left'});
        }).catch(function (e) {
            Notification.error({
                message: (e.data.message == null || e.data.message.startsWith('Required request body is')) ? "Error" : e.data.message,
                positionY: 'bottom',
                positionX: 'left'
            });
        });
    };
});

'use strict';

angular.module('revolovexApp')
    .config(function ($stateProvider, cdnLocation) {
        $stateProvider
            .state('term', {
                parent: 'home',
                url: '/term',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'term'
                },
                views: {
                    'homeContent@home': {
                        templateUrl: cdnLocation + 'content/term.html',
                        controller: 'TermController'
                    }
                }
            });
    });


'use strict';

angular.module('revolovexApp')
    .factory('Account', function Account($resource) {
        return $resource('api/account', {}, {
            'get': {
                method: 'GET', params: {}, isArray: false,
                interceptor: {
                    response: function (response) {
                        return response;
                    }
                }
            }
        });
    });


'use strict';

angular.module('revolovexApp')
    .factory('Activate', function ($resource) {
        return $resource('api/activate', {}, {
            'get': {method: 'GET', params: {}, isArray: false}
        });
    });


'use strict';

angular.module('revolovexApp')
    .factory('Auth', function Auth($rootScope, $state, $q, Activate, Principal, AuthServerProvider, Register) {
        return {
            login: function (credentials, callback) {
                var cb = callback || angular.noop;
                var deferred = $q.defer();

                AuthServerProvider.login(credentials).then(function (data) {
                    // retrieve the logged account information
                    Principal.identity(true).then(function (account) {

                        deferred.resolve(data);
                    });
                    return cb();
                }).catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

                return deferred.promise;
            },

            logout: function () {
                AuthServerProvider.logout();
                Principal.authenticate(null);
            },

            authorize: function (force) {
                return Principal.identity(force)
                    .then(function () {
                        var isAuthenticated = Principal.isAuthenticated();


                        if ($rootScope.toState.data.roles && $rootScope.toState.data.roles.length > 0 && !Principal.isInAnyRole($rootScope.toState.data.roles)) {
                            if (isAuthenticated) {
                                // user is signed in but not authorized for desired state
                                $state.go('accessdenied');
                            }
                            else {
                                // user is not authenticated. stow the state they wanted before you
                                // send them to the signin state, so you can return them when you're done
                                $rootScope.returnToState = $rootScope.toState;
                                $rootScope.returnToStateParams = $rootScope.toStateParams;

                                // now, send them to the signin state so they can log in
                                $state.go('login');
                            }
                        }
                    });
            },

            createAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Register.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        this.logout();
                        return cb(err);
                    }.bind(this)).$promise;
            }
            ,

            activateAccount: function (key, callback) {
                var cb = callback || angular.noop;

                return Activate.get(key,
                    function (response) {
                        return cb(response);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            }

        };
    });
'use strict';

angular.module('revolovexApp')
    .factory('AuthServerProvider', function loginService($http, localStorageService, $window) {
        return {
            login: function (credentials) {
                var data = 'j_username=' + encodeURIComponent(credentials.username) +
                    '&j_password=' + encodeURIComponent(credentials.password) +
                    '&_spring_security_remember_me=' + credentials.rememberMe + '&submit=Login';
                return $http.post('api/authentication', data, {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function (response) {
                    localStorageService.set('token', $window.btoa(credentials.username + ':' + credentials.password));
                    return response;
                });
            },
            logout: function () {
                // logout from the server
                $http.post('api/logout').success(function (response) {
                    localStorageService.clearAll();
                    // to get a new csrf token call the api
                    $http.get('api/account');
                    return response;
                });
            },
            getToken: function () {
                var token = localStorageService.get('token');
                return token;
            },
            hasValidToken: function () {
                var token = this.getToken();
                return !!token;
            }
        };
    });
'use strict';

angular.module('revolovexApp')
    .directive('hasAnyRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.isInAnyRole(roles);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    roles = attrs.hasAnyRole.replace(/\s+/g, '').split(',');

                if (roles.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
    .directive('hasRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.isInRole(role);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    role = attrs.hasRole.replace(/\s+/g, '');

                if (role.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }]);
'use strict';

angular.module('revolovexApp')
    .factory('Principal', function Principal($q, Account) {
        var _identity, _authenticated = false;

        return {
            isIdentityResolved: function () {
                return angular.isDefined(_identity);
            },
            isAuthenticated: function () {
                return _authenticated;
            },
            isInRole: function (role) {
                if (!_authenticated || !_identity || !_identity.roles) {
                    return false;
                }
                return _identity.roles.indexOf(role) !== -1;
            },
            isInAnyRole: function (roles) {
                if (!_authenticated || !_identity.roles) {
                    return false;
                }

                for (var i = 0; i < roles.length; i++) {
                    if (this.isInRole(roles[i])) {
                        return true;
                    }
                }

                return false;
            },
            authenticate: function (identity) {
                _identity = identity;
                _authenticated = identity !== null;
            },
            identity: function (force) {
                var deferred = $q.defer();

                if (force === true) {
                    _identity = undefined;
                }

                if (angular.isDefined(_identity)) {
                    deferred.resolve(_identity);

                    return deferred.promise;
                }

                Account.get().$promise
                    .then(function (account) {
                        _identity = account.data;
                        _authenticated = true;
                        deferred.resolve(_identity);
                    })
                    .catch(function () {
                        _identity = null;
                        _authenticated = false;
                        deferred.resolve(_identity);
                    });
                return deferred.promise;
            }

        };
    });
'use strict';

angular.module('revolovexApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {});
    });