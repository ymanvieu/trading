/** jest.config.js */

module.exports = {
    preset: 'jest-preset-angular',
    collectCoverageFrom: [
        '<rootDir>/src/app/**/*.ts',
        '!<rootDir>/src/app/**/en.ts',
        '!<rootDir>/src/app/**/*.module.ts'
    ],

    coverageDirectory: 'coverage',

    coverageReporters: [
        'lcov',
        'text-summary'
    ],

    testPathIgnorePatterns: [
        '<rootDir>/coverage/',
        '<rootDir>/dist/',
        '<rootDir>/e2e/',
        '<rootDir>/cypress/',
        '<rootDir>/node_modules/',
        '<rootDir>/src/app/*.(js|scss)'
    ],

    testMatch: [
        '<rootDir>/src/app/*.spec.ts',
        '<rootDir>/src/app/**/*.spec.ts'
    ],
};
